package org.ua.drmp.company.dto;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfficeViewDto {
	private Long id;
	private String locationName;
	private String workSchedule;
	private String additionalDescription;
	private Double latitude;
	private Double longitude;
	private Integer regionId;
	private Long companyId;
	private String companyName;
	private Set<Long> serviceIds;
	private Set<Long> categoryIds;
	private Set<Long> conditionIds;
	private List<CustomFieldValueDto> customFields;
	private String city;
	private Boolean isFree;
}