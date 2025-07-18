package org.ua.drmp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.repo.UserRepository;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;

	public boolean isOwner(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user -> user.getId().equals(userId))
			.orElse(false);
	}

	public boolean isAdminOrOwnerOrEditor(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user -> user.getId().equals(userId)
				|| user.hasRole("ADMIN") || user.hasRole("EDITOR"))
			.orElse(false);
	}

	public boolean isAdminOrOwnerOrEditorCompany(Authentication authentication, Long companyId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (company.getUser() == null) {
			return userRepository.findByEmail(authentication.getName())
				.map(user -> user.hasRole("ADMIN"))
				.orElse(false);
		}

		return userRepository.findByEmail(authentication.getName())
			.map(user -> user.getId().equals(company.getUser().getId())
				|| user.hasRole("ADMIN")
				|| user.hasRole("EDITOR"))
			.orElse(false);
	}
}
