package org.ua.drmp.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ua.drmp.entity.Token;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
	@Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
	List<Token> findAllValidTokensByUser(Long userId);

	@Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.expired = false AND t.revoked = false AND t.refreshToken = false")
	List<Token> findAllValidAccessTokensByUser(@Param("userId") Long userId);
	Optional<Token> findByToken(String token);
}