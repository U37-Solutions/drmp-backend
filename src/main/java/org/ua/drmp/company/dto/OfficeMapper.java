package org.ua.drmp.company.dto;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.ResourceNotFoundException;

@Component
public class OfficeMapper {

	public OfficeDto toDto(Office office) {
		Integer regionId = office.getRegionId();
		if (!RegionConst.regions.containsKey(regionId)) {
			throw new ResourceNotFoundException("Region not found");
		}
		return OfficeDto.builder()
			.id(office.getId())
			.workSchedule(office.getWorkSchedule())
			.donorSupport(office.getDonorSupport())
			.additionalDescription(office.getAdditionalDescription())
			.locationName(office.getLocationName())
			.latitude(office.getLatitude())
			.longitude(office.getLongitude())
			.regionId(regionId)
			.companyId(office.getCompany().getId())
			.serviceIds(office.getServices().stream().map(ServiceOffice::getId).collect(Collectors.toSet()))
			.categoryIds(office.getCategories().stream().map(Category::getId).collect(Collectors.toSet()))
			.conditionIds(office.getConditions().stream().map(Condition::getId).collect(Collectors.toSet()))
			.build();
	}

	public Office toEntity(OfficeDto dto,
		Company company,
		Set<ServiceOffice> services,
		Set<Category> categories,
		Set<Condition> conditions,
		User user) {
		Integer regionId = dto.getRegionId();
		if (!RegionConst.regions.containsKey(regionId)) {
			throw new ResourceNotFoundException("Region not found");
		}
		return Office.builder()
			.id(dto.getId())
			.workSchedule(dto.getWorkSchedule())
			.donorSupport(dto.getDonorSupport())
			.additionalDescription(dto.getAdditionalDescription())
			.locationName(dto.getLocationName())
			.latitude(dto.getLatitude())
			.longitude(dto.getLongitude())
			.regionId(regionId)
			.company(company)
			.user(user)
			.services(services)
			.categories(categories)
			.conditions(conditions)
			.build();
	}
}

