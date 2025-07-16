package org.ua.drmp.company.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDto {
	private Long id;
	private String workSchedule;
	private String donorSupport;
	private String additionalDescription;
	private String locationName;
	private Double latitude;
	private Double longitude;

	private Integer regionId;
	private Long companyId;

	private Set<Long> serviceIds;
	private Set<Long> categoryIds;
	private Set<Long> conditionIds;
}
