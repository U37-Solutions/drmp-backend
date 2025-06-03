package org.ua.drmp.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ua.drmp.entity.DRMPRole;
import org.ua.drmp.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(DRMPRole name);
}
