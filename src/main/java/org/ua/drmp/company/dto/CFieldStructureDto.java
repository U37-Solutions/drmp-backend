package org.ua.drmp.company.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ua.drmp.company.entity.CFieldStructureType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CFieldStructureDto {
	private Long id;
	private CFieldStructureType type;
	private String title;
	private String placeholder;
	private String tooltip;
	private Boolean required;
	private List<String> options;
}