package org.ua.drmp.open.city;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CityEntryDto {
	@JsonProperty("level_1")
	private Long level1;

	@JsonProperty("level_2")
	private Long level2;

	@JsonProperty("level_3")
	private String level3;

	@JsonProperty("level_4")
	private String level4;

	@JsonProperty("object_category")
	private String objectCategory;

	@JsonProperty("object_name")
	private String objectName;

	@JsonProperty("object_code")
	private Long objectCode;

	private String region;
	private String community;
}

