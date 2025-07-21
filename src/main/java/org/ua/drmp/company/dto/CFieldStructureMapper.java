package org.ua.drmp.company.dto;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.ua.drmp.company.entity.CFieldStructure;

@Component
@AllArgsConstructor
public class CFieldStructureMapper {

	public CFieldStructureDto toDto(CFieldStructure entity) {
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
}

