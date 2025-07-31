package org.ua.drmp.company.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.CompanySocial;
import org.ua.drmp.company.entity.CompanyStatus;
import org.ua.drmp.company.entity.CompanyType;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.entity.User;
import org.ua.drmp.open.PublicCompanyDto;

@Component
public class CompanyMapper {

	public CompanyDto toDto(Company company) {
		return CompanyDto.builder()
			.id(company.getId())
			.name(company.getName())
			.code(company.getCode())
			.contactName(company.getContactName())
			.ownershipType(company.getOwnershipType())
			.donorSupport(company.getDonorSupport())
			.phone(company.getPhone())
			.email(company.getEmail())
			.status(company.getStatus().name())
			.companyTypeId(company.getCompanyType().getId())
			.userIds(company.getUsers().stream()
				.map(User::getId)
				.toList())
			.socials(company.getSocials().stream()
				.map(this::mapSocial)
				.toList())
			.build();
	}

	public PublicCompanyDto toPublicDto(Company company) {
		return new PublicCompanyDto(company.getId(), company.getName());
	}


	public Company toEntity(CompanyDto dto, CompanyType type, Set<User> users, List<CompanySocial> socials, List<Office> offices) {
		return Company.builder()
			.id(dto.getId())
			.name(dto.getName())
			.code(dto.getCode())
			.contactName(dto.getContactName())
			.ownershipType(dto.getOwnershipType())
			.donorSupport(dto.getDonorSupport())
			.phone(dto.getPhone())
			.email(dto.getEmail())
			.status(CompanyStatus.valueOf(dto.getStatus()))
			.companyType(type)
			.users(users)
			.offices(offices != null ? offices : new ArrayList<>())
			.socials(socials != null ? socials : new ArrayList<>())
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

