package org.ua.drmp.company.service;

import java.util.List;
import org.ua.drmp.company.dto.OfficeDto;
import org.ua.drmp.company.dto.OfficeViewDto;

public interface OfficeService {

	List<OfficeViewDto> fetchAllOfficeByCompanyId(Long companyId);
	OfficeViewDto getOffice(Long officeId);

	OfficeDto createOffice(OfficeDto dto);

	OfficeDto updateOffice(Long officeId, OfficeDto dto);

	void deleteOffice(Long officeId);

	List<OfficeViewDto> fetchAllOffices();
}
