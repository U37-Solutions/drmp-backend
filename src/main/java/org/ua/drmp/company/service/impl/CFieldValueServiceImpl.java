package org.ua.drmp.company.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.company.dto.CFieldValueDto;
import org.ua.drmp.company.entity.CFieldStructure;
import org.ua.drmp.company.entity.CFieldStructureType;
import org.ua.drmp.company.entity.CFieldValue;
import org.ua.drmp.company.repo.CFieldStructureRepository;
import org.ua.drmp.company.repo.CFieldValueRepository;
import org.ua.drmp.company.service.CFieldValueService;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CFieldValueServiceImpl implements CFieldValueService {

	private final CFieldValueRepository cFieldValueRepository;
	private final CFieldStructureRepository cFieldStructureRepository;

	@Override
	public CFieldValueDto create(CFieldValueDto dto) {
		CFieldStructure structure = cFieldStructureRepository.findById(dto.getStructureId())
			.orElseThrow(() -> new ResourceNotFoundException("Structure not found"));

		validateOptions(dto, structure);

		CFieldValue value = CFieldValue.builder()
			.value(dto.getValue())
			.structure(structure)
			.build();

		CFieldValue saved = cFieldValueRepository.save(value);
		return mapToDto(saved);
	}
	@Override
	public CFieldValueDto update(Long id, CFieldValueDto dto) {
		CFieldValue existing = cFieldValueRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("CFieldValue not found"));

		CFieldStructure structure = existing.getStructure();

		// Якщо structureId передається — оновлюємо структуру
		if (dto.getStructureId() != null && !dto.getStructureId().equals(existing.getStructure().getId())) {
			structure = cFieldStructureRepository.findById(dto.getStructureId())
				.orElseThrow(() -> new ResourceNotFoundException("Structure not found"));
			existing.setStructure(structure);
		}

		validateOptions(dto, structure);

		existing.setValue(dto.getValue());

		CFieldValue updated = cFieldValueRepository.save(existing);
		return mapToDto(updated);
	}


	@Override
	public void delete(Long id) {
		if (!cFieldValueRepository.existsById(id)) {
			throw new ResourceNotFoundException("CFieldValue not found");
		}
		cFieldValueRepository.deleteById(id);
	}

	@Override
	public CFieldValueDto fetchById(Long id) {
		return cFieldValueRepository.findById(id)
			.map(this::mapToDto)
			.orElseThrow(() -> new ResourceNotFoundException("CFieldValue not found"));
	}

	@Override
	public List<CFieldValueDto> fetchAll() {
		return cFieldValueRepository.findAll()
			.stream()
			.map(this::mapToDto)
			.toList();
	}

	private CFieldValueDto mapToDto(CFieldValue entity) {
		CFieldValueDto dto = new CFieldValueDto();
		dto.setId(entity.getId());
		dto.setValue(entity.getValue());
		dto.setStructureId(entity.getStructure().getId());
		return dto;
	}
	private static void validateOptions(CFieldValueDto dto, CFieldStructure structure) {
		if (structure.getType() == CFieldStructureType.SELECT) {
			List<String> options = structure.getOptions();
			if (options == null || options.isEmpty()) {
				throw new ResourceNotFoundException("No options defined for SELECT structure");
			}
			if (!options.contains(dto.getValue())) {
				throw new ResourceNotFoundException("Invalid value: must be one of " + options);
			}
		}
	}
}
