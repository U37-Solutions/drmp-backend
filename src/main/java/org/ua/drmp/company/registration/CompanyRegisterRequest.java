package org.ua.drmp.company.registration;

import java.util.List;
import lombok.Data;
import org.ua.drmp.company.dto.CompanySocialDto;
import org.ua.drmp.company.dto.OfficeDto;

@Data
public class CompanyRegisterRequest {
	private String name;
	private String code;
	private String contactName;
	private String phone;
	private String email;
	private Long companyTypeId;
	private List<CompanySocialDto> socials;
	private List<OfficeDto> offices;
}
