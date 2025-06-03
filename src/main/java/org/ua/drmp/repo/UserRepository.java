package org.ua.drmp.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ua.drmp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
}
