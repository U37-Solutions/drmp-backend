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
	private Long companyId;
	private String regionName;
	private Set<String> services;
	private Set<String> categories;
	private Set<String> conditions;
	private List<CustomFieldValueDto> customFields;
	private String city;
	private Boolean isFree;

	// Company fields
	private String companyName;
	private String contactName;
	private String phone;
	private String email;
	private List<CompanySocialDto> socials;
}

