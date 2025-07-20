package org.ua.drmp.company.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "office_cfield_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(OfficeCFieldValueId.class)
public class OfficeCFieldValue {

	@Id
	@ManyToOne
	@JoinColumn(name = "office_id")
	private Office office;

	@Id
	@ManyToOne
	@JoinColumn(name = "cfield_values_id")
	private CFieldValue value;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OfficeCFieldValue that)) return false;

		return Objects.equals(
			office != null ? office.getId() : null,
			that.office != null ? that.office.getId() : null
		) && Objects.equals(
			value != null ? value.getId() : null,
			that.value != null ? that.value.getId() : null
		);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			office != null ? office.getId() : null,
			value != null ? value.getId() : null
		);
	}
}
