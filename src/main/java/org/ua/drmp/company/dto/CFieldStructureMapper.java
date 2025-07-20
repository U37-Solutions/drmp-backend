package org.ua.drmp.company.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.CFieldStructure;
import org.ua.drmp.company.repo.CFieldStructureRepository;
import org.ua.drmp.exception.BadRequestException;

@Component
@AllArgsConstructor
public class CFieldStructureMapper {
	private final CFieldStructureRepository cFieldStructureRepository;

	public CFieldStructureDto toDto(CFieldStructure entity) {
		titleValidation(entity.getTitle());
		return CFieldStructureDto.builder()
			.id(entity.getId())
			.type(entity.getType())
			.title(entity.getTitle())
			.placeholder(entity.getPlaceholder())
			.tooltip(entity.getTooltip())
			.required(entity.getRequired())
			.options(entity.getOptions())
			.build();
	}

	public CFieldStructure toEntity(CFieldStructureDto dto) {
		titleValidation(dto.getTitle());
		return CFieldStructure.builder()
			.id(dto.getId())
			.type(dto.getType())
			.title(dto.getTitle())
			.placeholder(dto.getPlaceholder())
			.tooltip(dto.getTooltip())
			.required(dto.getRequired())
			.options(dto.getOptions() != null ? dto.getOptions() : new ArrayList<>())
			.build();
	}

	private void titleValidation(String dto) {
		List<CFieldStructure> fieldStructureList = cFieldStructureRepository.findAll();

		boolean titleExists = fieldStructureList.stream()
			.anyMatch(field -> StringUtils.equals(field.getTitle(), dto));

		if (titleExists) {
			throw new BadRequestException("Field with this title already exists");
		}
	}
}

