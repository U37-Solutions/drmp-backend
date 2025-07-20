package org.ua.drmp.company.service;

import java.util.List;
import org.ua.drmp.company.dto.OfficeDto;

public interface OfficeService {

	List<OfficeDto> fetchAllOfficeByCompanyId(Long companyId);
	OfficeDto getOffice(Long officeId);

	OfficeDto createOffice(OfficeDto dto);

	OfficeDto updateOffice(Long officeId, OfficeDto dto);

	void deleteOffice(Long officeId);

	List<OfficeDto> fetchAllOffices();
}
