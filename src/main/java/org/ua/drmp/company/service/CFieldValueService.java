package org.ua.drmp.company.service;

import java.util.List;
import org.ua.drmp.company.dto.CFieldValueDto;

public interface CFieldValueService {
	CFieldValueDto fetchById(Long id);

	List<CFieldValueDto> fetchAll();

	CFieldValueDto create(CFieldValueDto dto);

	CFieldValueDto update(Long id, CFieldValueDto dto);

	void delete(Long id);

}
