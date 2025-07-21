package org.ua.drmp.company.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ua.drmp.company.dto.CFieldStructureDto;
import org.ua.drmp.company.dto.CFieldStructureMapper;
import org.ua.drmp.company.entity.CFieldStructure;
import org.ua.drmp.company.entity.CFieldStructureType;
import org.ua.drmp.company.repo.CFieldStructureRepository;
import org.ua.drmp.company.service.CFieldStructureService;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CFieldStructureServiceImpl implements CFieldStructureService {

	private final CFieldStructureRepository repository;
	private final CFieldStructureMapper mapper;

	@Override
	public List<CFieldStructureDto> fetchAll() {
		return repository.findAll().stream().map(mapper::toDto).toList();
	}

	@Override
	public CFieldStructureDto fetchById(Long id) {
		return repository.findById(id)
			.map(mapper::toDto)
			.orElseThrow(() -> new ResourceNotFoundException("CFieldStructure not found"));
	}

	@Override
	public CFieldStructureDto create(CFieldStructureDto dto) {
		titleValidation(dto.getTitle());
		return mapper.toDto(repository.save(mapper.toEntity(dto)));
	}

	@Override
	@Transactional
	public CFieldStructureDto update(Long id, CFieldStructureDto dto) {
		titleValidation(dto.getTitle());
		CFieldStructure existing = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("CFieldStructure not found"));

		existing.setTitle(dto.getTitle());
		existing.setPlaceholder(dto.getPlaceholder());
		existing.setTooltip(dto.getTooltip());
		existing.setRequired(dto.getRequired());
		existing.setType(dto.getType());

		// якщо type == SELECT -> оновлюємо options, інакше null
		if (dto.getType() == CFieldStructureType.SELECT) {
			existing.setOptions(dto.getOptions());
		} else {
			existing.setOptions(null);
		}

		return mapper.toDto(repository.save(existing));
	}


	@Override
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("CFieldStructure not found");
		}
		repository.deleteById(id);
	}

	private void titleValidation(String title) {
		List<CFieldStructure> fieldStructureList = repository.findAll();

		boolean titleExists = fieldStructureList.stream()
			.anyMatch(field -> StringUtils.equals(field.getTitle(), title));

		if (titleExists) {
			throw new BadRequestException("Field with this title already exists");
		}
	}
}
