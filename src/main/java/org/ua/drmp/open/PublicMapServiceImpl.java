package org.ua.drmp.open;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.CompanyMapper;
import org.ua.drmp.company.dto.CustomFieldValueDto;
import org.ua.drmp.company.dto.RegionConst;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.ServiceOffice;

@Service
@RequiredArgsConstructor
public class PublicMapServiceImpl implements PublicMapService {

	private final PublicOfficeRepository officeRepository;
	private final CompanyMapper companyMapper;

	@Override
	public List<PublicMapPointDto> searchOffices(String searchBy, String search,
		Long regionId, String city,
		List<Long> categoryIds, List<Long> serviceIds,
		Boolean isFree,
		Optional<double[]> boundaries) {

		boolean anyFilterPresent =
			(search != null && !search.isBlank()) ||
				(regionId != null) ||
				(city != null && !city.isBlank()) ||
				(categoryIds != null && !categoryIds.isEmpty()) ||
				(serviceIds != null && !serviceIds.isEmpty()) ||
				isFree != null;

		List<Office> offices;

		if (anyFilterPresent) {
			offices = officeRepository.searchWithFilters(
				searchBy, search, regionId, city, categoryIds, serviceIds, isFree, Optional.empty()
			);
		} else {
			offices = boundaries.filter(b -> b.length == 4)
				.map(b -> officeRepository.findOfficesWithinBounds(b[0], b[1], b[2], b[3]))
				.orElseGet(officeRepository::findAllWithCoordinates);
		}

		return offices.stream()
			.map(this::mapToDto)
			.toList();
	}


	private PublicMapPointDto mapToDto(Office office) {
		Company company = office.getCompany();

		return PublicMapPointDto.builder()
			// Office fields
			.id(office.getId())
			.locationName(office.getLocationName())
			.workSchedule(office.getWorkSchedule())
			.additionalDescription(office.getAdditionalDescription())
			.latitude(office.getLatitude())
			.longitude(office.getLongitude())
			.regionName(RegionConst.regions.get(office.getRegionId()))
			.services(office.getServices().stream()
				.map(ServiceOffice::getName)
				.collect(Collectors.toSet()))
			.categories(office.getCategories().stream()
				.map(Category::getName)
				.collect(Collectors.toSet()))
			.conditions(office.getConditions().stream()
				.map(Condition::getName)
				.collect(Collectors.toSet()))
			.customFields(office.getCustomFieldValues().stream()
				.map(ofv -> CustomFieldValueDto.builder()
					.structureId(ofv.getValue().getStructure().getId())
					.value(ofv.getValue().getValue())
					.build())
				.toList())
			.city(office.getCity())
			.isFree(office.getIsFree())

			// Company fields
			.companyName(company.getName())
			.contactName(company.getContactName())
			.phone(company.getPhone())
			.email(company.getEmail())
			.socials(company.getSocials().stream()
				.map(companyMapper::mapSocial)
				.toList())
			.build();
	}

}
