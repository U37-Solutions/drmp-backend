package org.ua.drmp.company.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.CFieldValue;
import org.ua.drmp.company.entity.Category;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.entity.Condition;
import org.ua.drmp.company.entity.Office;
import org.ua.drmp.company.entity.OfficeCFieldValue;
import org.ua.drmp.company.entity.ServiceOffice;
import org.ua.drmp.exception.ResourceNotFoundException;

@Component
public class OfficeMapper {

	private static final GeometryFactory geometryFactory = new GeometryFactory();

	public static Point createPoint(double lon, double lat) {
		return geometryFactory.createPoint(new Coordinate(lon, lat));
	}

	public OfficeDto toDto(Office office) {
		Integer regionId = office.getRegionId();
		if (!RegionConst.regions.containsKey(regionId)) {
			throw new ResourceNotFoundException("Region not found");
		}

		List<CustomFieldValueDto> customFields = office.getCustomFieldValues().stream()
			.map(ofv -> CustomFieldValueDto.builder()
				.structureId(ofv.getValue().getStructure().getId())
				.value(ofv.getValue().getValue())
				.build())
			.collect(Collectors.toList());

		return OfficeDto.builder()
			.id(office.getId())
			.workSchedule(office.getWorkSchedule())
			.additionalDescription(office.getAdditionalDescription())
			.locationName(office.getLocationName())
			.latitude(office.getLatitude())
			.longitude(office.getLongitude())
			.regionId(regionId)
			.companyId(office.getCompany().getId())
			.serviceIds(office.getServices().stream().map(ServiceOffice::getId).collect(Collectors.toSet()))
			.categoryIds(office.getCategories().stream().map(Category::getId).collect(Collectors.toSet()))
			.conditionIds(office.getConditions().stream().map(Condition::getId).collect(Collectors.toSet()))
			.customFields(customFields)
			.build();
	}

	public Office toEntityWithoutUser(OfficeDto dto,
		Company company,
		Set<ServiceOffice> services,
		Set<Category> categories,
		Set<Condition> conditions,
		Set<CFieldValue> cFieldValues) {

		Office office = baseOfficeBuild(dto, company, services, categories, conditions);
		office.setCustomFieldValues(mapCFieldValues(cFieldValues, office));
		return office;
	}

	private Office baseOfficeBuild(OfficeDto dto,
		Company company,
		Set<ServiceOffice> services,
		Set<Category> categories,
		Set<Condition> conditions) {

		Integer regionId = dto.getRegionId();
		if (!RegionConst.regions.containsKey(regionId)) {
			throw new ResourceNotFoundException("Region not found");
		}

		Office.OfficeBuilder builder = Office.builder()
			.id(dto.getId())
			.workSchedule(dto.getWorkSchedule())
			.additionalDescription(dto.getAdditionalDescription())
			.locationName(dto.getLocationName())
			.latitude(dto.getLatitude())
			.longitude(dto.getLongitude())
			.regionId(regionId)
			.company(company)
			.services(services)
			.categories(categories)
			.conditions(conditions);

		if (dto.getLatitude() != null && dto.getLongitude() != null) {
			builder.coordinates(createPoint(dto.getLongitude(), dto.getLatitude()));
		}

		return builder.build();
	}

	private List<OfficeCFieldValue> mapCFieldValues(Set<CFieldValue> cFieldValues, Office office) {
		return cFieldValues.stream()
			.map(value -> OfficeCFieldValue.builder()
				.office(office)
				.value(value)
				.build())
			.collect(Collectors.toList());
	}

	public OfficeViewDto toViewDto(Office office) {
		Integer regionId = office.getRegionId();
		if (!RegionConst.regions.containsKey(regionId)) {
			throw new ResourceNotFoundException("Region not found");
		}

		List<CustomFieldValueDto> customFields = office.getCustomFieldValues().stream()
			.map(ofv -> CustomFieldValueDto.builder()
				.structureId(ofv.getValue().getStructure().getId())
				.value(ofv.getValue().getValue())
				.build())
			.toList();

		return OfficeViewDto.builder()
			.id(office.getId())
			.locationName(office.getLocationName())
			.workSchedule(office.getWorkSchedule())
			.additionalDescription(office.getAdditionalDescription())
			.latitude(office.getLatitude())
			.longitude(office.getLongitude())
			.regionId(regionId)
			.companyId(office.getCompany().getId())
			.companyName(office.getCompany().getName())
			.serviceIds(office.getServices().stream().map(ServiceOffice::getId).collect(Collectors.toSet()))
			.categoryIds(office.getCategories().stream().map(Category::getId).collect(Collectors.toSet()))
			.conditionIds(office.getConditions().stream().map(Condition::getId).collect(Collectors.toSet()))
			.customFields(customFields)
			.build();
	}


}

