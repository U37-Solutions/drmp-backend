package org.ua.drmp.company.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "cfield_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CFieldStructure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type;
	private String title;
	private String placeholder;
	private Boolean required;

	@Type(JsonType.class)
	@Column(columnDefinition = "jsonb")
	private List<String> options = new ArrayList<>();
}
