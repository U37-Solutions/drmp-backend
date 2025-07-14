package org.ua.drmp.company.service;

import org.ua.drmp.company.dto.OfficeDto;

public interface OfficeService {
	OfficeDto getOffice(Long officeId);

	OfficeDto createOffice(Long companyId, OfficeDto dto);

	OfficeDto updateOffice(Long officeId, OfficeDto dto);

	void deleteOffice(Long officeId);
}
