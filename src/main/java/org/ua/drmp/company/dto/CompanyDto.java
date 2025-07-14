package org.ua.drmp.company.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
	private Long id;
	private String name;
	private String code;
	private String contactName;
	private String phone;
	private String email;
	private String status;
	private Long companyTypeId;
	private Long userId;
	private List<CompanySocialDto> socials;
}

