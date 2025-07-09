package org.ua.drmp.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.ua.drmp.company.Company;

@Getter
@Setter
@Entity
@Table(name = "chat")
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String accessToken; // UUID для клієнта

	@Column(nullable = false)
	private Instant createdAt = Instant.now();

	@Column(nullable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean archived = false;

	@ManyToOne(optional = false)
	private Company company;

	@Column(nullable = false)
	private boolean notifyCompanyUser = true;

	@Column(nullable = false)
	private Instant updatedAt = Instant.now();
}
