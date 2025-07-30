package org.ua.drmp.open;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import org.ua.drmp.company.dto.CompanySocialDto;
import org.ua.drmp.company.dto.CustomFieldValueDto;

@Data
@Builder
public class PublicMapPointDto {
	// Office fields
	private Long id;
	private String locationName;
	private String workSchedule;
	private String additionalDescription;
	private Double latitude;
	private Double longitude;
	private Integer regionId;
	private Long companyId;
	private Set<Long> serviceIds;
	private Set<Long> categoryIds;
	private Set<Long> conditionIds;
	private List<CustomFieldValueDto> customFields;

	// Company fields
	private String companyName;
	private String contactName;
	private String phone;
	private String email;
	private List<CompanySocialDto> socials;
}

