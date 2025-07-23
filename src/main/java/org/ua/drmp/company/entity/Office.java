package org.ua.drmp.company.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ua.drmp.entity.User;

@Entity
@Table(name = "offices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String workSchedule;
	private String additionalDescription;
	private String locationName;
	private Double latitude;
	private Double longitude;
	private Integer regionId;

	@ManyToOne
	@JoinColumn(name = "company_id")
	@JsonBackReference
	private Company company;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToMany
	@JoinTable(name = "office_services",
		joinColumns = @JoinColumn(name = "office_id"),
		inverseJoinColumns = @JoinColumn(name = "service_id"))
	private Set<ServiceOffice> services = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "office_categories",
		joinColumns = @JoinColumn(name = "office_id"),
		inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "office_conditions",
		joinColumns = @JoinColumn(name = "office_id"),
		inverseJoinColumns = @JoinColumn(name = "condition_id"))
	private Set<Condition> conditions = new HashSet<>();

	@OneToMany(mappedBy = "office", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OfficeCFieldValue> customFieldValues = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "user_offices",
		joinColumns = @JoinColumn(name = "office_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users = new HashSet<>();

	@Override
	public String toString() {
		return "Office{" +
			"locationName='" + locationName + '\'' +
			", workSchedule='" + workSchedule + '\'' +
			", additionalDescription='" + additionalDescription + '\'' +
			", latitude=" + latitude +
			", longitude=" + longitude +
			", regionId=" + regionId +
			", company=" + (company != null ? company.getName() : null) +
			", user=" + (user != null ? user.getEmail() : null) +
			", services=" + services.stream()
			.map(s -> s.getName() != null ? s.getName() : "unknown")
			.toList() +
			", categories=" + categories.stream()
			.map(c -> c.getName() != null ? c.getName() : "unknown")
			.toList() +
			", conditions=" + conditions.stream()
			.map(c -> c.getName() != null ? c.getName() : "unknown")
			.toList() +
			", customFieldValues=" + customFieldValues.stream()
			.map(v -> v.getValue() != null ? v.getValue().getValue() : "unknown")
			.toList() +
			'}';
	}

}

