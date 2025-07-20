package org.ua.drmp.company.service;

import java.util.List;
import org.ua.drmp.company.dto.CFieldStructureDto;

public interface CFieldStructureService {
	List<CFieldStructureDto> fetchAll();
	CFieldStructureDto fetchById(Long id);
	CFieldStructureDto create(CFieldStructureDto dto);
	CFieldStructureDto update(Long id, CFieldStructureDto dto);
	void delete(Long id);
}
