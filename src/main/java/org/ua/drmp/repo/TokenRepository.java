package org.ua.drmp.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ua.drmp.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
	@Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
	List<Token> findAllValidTokensByUser(Long userId);

	@Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.expired = false AND t.revoked = false AND t.refreshToken = false")
	List<Token> findAllValidAccessTokensByUser(@Param("userId") Long userId);
	Optional<Token> findByToken(String token);

	@Query("SELECT t FROM Token t WHERE t.user.id = :userId ORDER BY t.id ASC")
	List<Token> findAllByUserOrderByIdAsc(@Param("userId") Long userId);

	@Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.sessionId = :sessionId")
	List<Token> findAllByUserAndSessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);

}
