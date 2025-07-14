package org.ua.drmp.company.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeCFieldValueId implements Serializable {
	private Long office;
	private Long value;
}
