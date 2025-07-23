package org.ua.drmp.company.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String code;
	private String contactName;
	private String phone;
	private String email;
	private String donorSupport;
	@Column(nullable = false)
	private String ownershipType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CompanyStatus status;

	@ManyToOne
	@JoinColumn(name = "company_type_id")
	private CompanyType companyType;

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Set<User> users = new HashSet<>();

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<CompanySocial> socials = new ArrayList<>();

	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Office> offices = new ArrayList<>();

	@Override
	public String toString() {
		return "Company{" +
			"id=" + id +
			", name='" + name + '\'' +
			", code='" + code + '\'' +
			", contactName='" + contactName + '\'' +
			", phone='" + phone + '\'' +
			", email='" + email + '\'' +
			", donorSupport='" + donorSupport + '\'' +
			", ownershipType='" + ownershipType + '\'' +
			", status=" + status +
			", companyType=" + (companyType != null ? companyType.getName() : null) +
			", users=" + users.stream().map(user -> user.getEmail() != null ? user.getEmail() : "unknown").toList() +
			", offices=" + offices.stream().map(office -> office.getLocationName() != null ? office.getLocationName() : "unknown").toList() +
			", socials=" + socials.stream().map(s -> s.getUrl() != null ? s.getUrl() : "unknown").toList() +
			'}';
	}

}

