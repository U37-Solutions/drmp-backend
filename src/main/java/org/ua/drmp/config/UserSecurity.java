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

	public boolean isAdminOrOwner(Authentication authentication, Long userId) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.map(user -> user.hasRole("ADMIN") || user.getId().equals(userId))
			.orElse(false);
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
	public boolean isCompanyUserOrAbove(Authentication authentication, Long companyId) {
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
}
