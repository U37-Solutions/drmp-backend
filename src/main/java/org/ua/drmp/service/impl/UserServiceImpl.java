package org.ua.drmp.service.impl;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.dto.ChangePasswordRequest;
import org.ua.drmp.dto.ConfirmRegistrationRequest;
import org.ua.drmp.dto.InviteCompanyUserRequest;
import org.ua.drmp.dto.InviteUserRequest;
import org.ua.drmp.dto.UserRequest;
import org.ua.drmp.dto.UserResponse;
import org.ua.drmp.dto.UserSessionResponse;
import org.ua.drmp.entity.DRMPRole;
import org.ua.drmp.entity.Role;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.ForbiddenOperationException;
import org.ua.drmp.exception.InvalidPasswordException;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.exception.UserNotFoundException;
import org.ua.drmp.logging.ChangeLogService;
import org.ua.drmp.repo.RoleRepository;
import org.ua.drmp.repo.TokenRepository;
import org.ua.drmp.repo.UserRepository;
import org.ua.drmp.service.EmailService;
import org.ua.drmp.service.InviteTokenService;
import org.ua.drmp.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;
	private final InviteTokenService inviteTokenService;
	private final ChangeLogService changelogService;

	@Override
	public void changePassword(ChangePasswordRequest changePasswordRequest) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
			throw new InvalidPasswordException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
		userRepository.save(user);
	}

	@Override
	public void resetPassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Override
	public List<UserResponse> fetchUsers(DRMPRole role) {
		List<User> users;
		if (role == null) {
			users = userRepository.findAll();
		} else {
			users = userRepository.findAllByRoles_Name(role);
		}
		return users.stream()
			.map(this::mapToResponse)
			.toList();
	}

	@Override
	public User fetchByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	@Override
	public List<UserResponse> fetchUsersByCompanyId(Long companyId) {
		List<User> users = userRepository.findAllByCompany_Id(companyId);
		return users.stream()
			.map(user -> new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getFirstName(),
				user.getLastName()
			))
			.toList();
	}

	@Override
	public UserResponse fetchUserById(Long id) {
		return mapToResponse(userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("User not found")));
	}

	@Override
	public void updateUser(Long id, UserRequest request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
		User oldUser = User.builder()
			.email(user.getEmail())
			.lastName(user.getLastName())
			.firstName(user.getFirstName()).build();
		String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!user.getEmail().equals(currentEmail)) {
			throw new ForbiddenOperationException("You are not allowed to update this user");
		}

		if (request.email() != null) {
			user.setEmail(request.email());
		}
		if (request.password() != null) {
			user.setPassword(passwordEncoder.encode(request.password()));
		}
		if (request.firstName() != null) {
			user.setFirstName(request.firstName());
		}
		if (request.lastName() != null) {
			user.setLastName(request.lastName());
		}
		userRepository.save(user);

			changelogService.logUserChange(
				currentEmail,
				"update",
				oldUser,
				user
			);
	}

	@Override
	public void deleteUserById(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
		tokenRepository.deleteAll(tokenRepository.findAllValidTokensByUser(id));
		userRepository.deleteById(id);

		changelogService.logUserChange(
			SecurityContextHolder.getContext().getAuthentication().getName(),
			"delete",
			user,
			null
		);
	}

	@Override
	public UserSessionResponse sessionInfo() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
		DRMPRole drmpRole = user.getRoles().stream().findFirst()
			.orElseThrow(() -> new ResourceNotFoundException("Role not found")).getName();

		Long companyId = null;
		if (drmpRole == DRMPRole.COMPANY_ADMIN || drmpRole == DRMPRole.COMPANY_USER) {
			Company company = user.getCompany();
			if (company == null) {
				throw new ResourceNotFoundException("Company not assigned to user");
			}
			companyId = company.getId();
		}
		return new UserSessionResponse(
			user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), drmpRole, companyId

		);
	}

	@Override
	public void inviteUser(InviteUserRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new BadRequestException("User with email " + request.email() + "already exist");
		}

		String token = inviteTokenService.createInviteToken(request);
		emailService.sendInviteUserEmail(request.email(), token);
	}

	@Override
	public void inviteCompanyUser(InviteCompanyUserRequest request) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User companyAdmin = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));
		if (userRepository.existsByEmail(request.email())) {
			throw new BadRequestException("User with email " + request.email() + " already exists");
		}
		User user = new User();
		Role role = roleRepository.findByName(DRMPRole.COMPANY_USER)
			.orElseThrow(() -> new ResourceNotFoundException("Role not found"));
		user.setEmail(request.email());
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRoles(Set.of(role));
		user.setCompany(companyAdmin.getCompany());

		userRepository.save(user);
		changelogService.logUserChange(
			SecurityContextHolder.getContext().getAuthentication().getName(),
			"create",
			null,
			user
		);
		emailService.sendInviteForCompanyUser(request.email(), request.password());
	}

	@Override
	public InviteUserRequest getTemporaryUserData(String token) {
		InviteUserRequest request = inviteTokenService.getUserDataByToken(token);

		return new InviteUserRequest(
			request.email(),
			request.role(),
			request.firstName(),
			request.lastName()
		);
	}

	@Override
	public void confirmRegistration(ConfirmRegistrationRequest request) {
		InviteUserRequest inviteData = inviteTokenService.getUserDataByToken(request.token());

		Role role = roleRepository.findByName(DRMPRole.valueOf(inviteData.role()))
			.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		User user = new User();
		user.setEmail(inviteData.email());
		user.setFirstName(
			request.firstName() != null ? request.firstName() : inviteData.firstName()
		);
		user.setLastName(
			request.lastName() != null ? request.lastName() : inviteData.lastName()
		);
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRoles(Set.of(role));

		userRepository.save(user);
		inviteTokenService.invalidateInviteToken(request.token());

		emailService.sendSuccessfulRegistrationEmail(user.getEmail());

		changelogService.logUserChange(
			SecurityContextHolder.getContext().getAuthentication().getName(),
			"create",
			null,
			user
		);
	}

	private UserResponse mapToResponse(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
	}
}
