package org.ua.drmp.config;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.company.repo.OfficeRepository;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.repo.UserRepository;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;
	private final OfficeRepository officeRepository;

	public boolean isOwner(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user -> user.getId().equals(userId))
			.orElse(false);
	}

	public boolean isAdminOrOwner(Authentication authentication, Long targetUserId) {
		String email = authentication.getName();

		Optional<User> optionalCurrentUser = userRepository.findByEmail(email);
		Optional<User> optionalTargetUser = userRepository.findById(targetUserId);

		if (optionalCurrentUser.isEmpty() || optionalTargetUser.isEmpty()) {
			return false;
		}

		User currentUser = optionalCurrentUser.get();
		User targetUser = optionalTargetUser.get();

		// admin
		if (currentUser.hasRole("ADMIN")) {
			return true;
		}

		// owned
		if (currentUser.getId().equals(targetUserId)) {
			return true;
		}

		// CA can delete CU
		return currentUser.hasRole("COMPANY_ADMIN")
			&& targetUser.hasRole("COMPANY_USER")
			&& currentUser.getCompany().getId().equals(targetUser.getCompany().getId());
	}


	public boolean isAdminOrOwnerOrEditor(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user ->
				user.getId().equals(userId)
					|| user.hasRole("ADMIN")
					|| user.hasRole("EDITOR"))
			.orElse(false);
	}

	/**
	 * Доступ мають ADMIN або COMPANY_ADMIN (власник компанії)
	 */
	public boolean isAdminOrCompanyAdmin(Authentication authentication, Long companyId) {
		String email = authentication.getName();
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		return userRepository.findByEmail(email)
			.map(user ->
				user.hasRole("ADMIN") ||
					(company.getUsers().contains(user) && user.hasRole("COMPANY_ADMIN"))
			)
			.orElse(false);
	}

	/**
	 * Доступ мають ADMIN, EDITOR або COMPANY_ADMIN (власник компанії)
	 */
	public boolean isAdminEditorOrCompanyAdmin(Authentication authentication, Long companyId) {
		String email = authentication.getName();
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		return userRepository.findByEmail(email)
			.map(user ->
				user.hasRole("ADMIN") ||
					user.hasRole("EDITOR") ||
					(company.getUsers().contains(user) && user.hasRole("COMPANY_ADMIN"))
			)
			.orElse(false);
	}

	/**
	 * Доступ мають ADMIN, EDITOR, COMPANY_ADMIN або COMPANY_USER
	 */
	public boolean isCompanyUserOrAboveByCompanyId(Authentication authentication, Long companyId) {
		String email = authentication.getName();
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		return userRepository.findByEmail(email)
			.map(user ->
				user.hasRole("ADMIN") ||
					user.hasRole("EDITOR") ||
					(company.getUsers().contains(user) && (
						user.hasRole("COMPANY_ADMIN") || user.hasRole("COMPANY_USER"))
					)
			)
			.orElse(false);
	}

	public boolean isCompanyUserOrAboveByOfficeId(Authentication authentication, Long officeId) {
		String email = authentication.getName();

		Office office = officeRepository.findById(officeId)
			.orElseThrow(() -> new ResourceNotFoundException("Office not found"));

		Company company = office.getCompany();

		return userRepository.findByEmail(email)
			.map(user ->
				user.hasRole("ADMIN") ||
					user.hasRole("EDITOR") ||
					(company.getUsers().contains(user) &&
						(user.hasRole("COMPANY_ADMIN") || user.hasRole("COMPANY_USER")))
			)
			.orElse(false);
	}

}
