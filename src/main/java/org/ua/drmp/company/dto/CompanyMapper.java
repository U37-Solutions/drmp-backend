package org.ua.drmp.company.dto;

import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.CompanySocial;
import org.ua.drmp.company.entity.CompanyStatus;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.entity.User;

@Component
public class CompanyMapper {

	public CompanyDto toDto(Company company) {
		return CompanyDto.builder()
			.id(company.getId())
			.name(company.getName())
			.code(company.getCode())
			.contactName(company.getContactName())
			.phone(company.getPhone())
			.email(company.getEmail())
			.status(company.getStatus().name())
			.companyTypeId(company.getCompanyType().getId())
			.userId(company.getUser() != null ? company.getUser().getId() : null)
			.socials(company.getSocials().stream()
				.map(this::mapSocial)
				.toList())
			.build();
	}

	public Company toEntity(CompanyDto dto, CompanyType type, User user) {
		return Company.builder()
			.id(dto.getId())
			.name(dto.getName())
			.code(dto.getCode())
			.contactName(dto.getContactName())
			.phone(dto.getPhone())
			.email(dto.getEmail())
			.status(CompanyStatus.valueOf(dto.getStatus()))
			.companyType(type)
			.user(user)
			.build();
	}

	public CompanySocialDto mapSocial(CompanySocial social) {
		return CompanySocialDto.builder()
			.type(social.getType())
			.url(social.getUrl())
			.build();
	}

	public CompanySocial toSocialEntity(CompanySocialDto dto, Company company) {
		return CompanySocial.builder()
			.type(dto.getType())
			.url(dto.getUrl())
			.company(company)
			.build();
	}
}

