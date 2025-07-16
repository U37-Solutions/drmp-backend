package org.ua.drmp.chat.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ua.drmp.chat.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	Optional<Chat> findByAccessToken(String accessToken);
	@Query("SELECT c FROM Chat c JOIN FETCH c.company WHERE c.archived = false ORDER BY c.updatedAt DESC")
	List<Chat> findActiveChatsWithCompany();
	List<Chat> findTop50ByArchivedTrueOrderByExpiresAtDesc();
	List<Chat> findByArchivedFalseAndExpiresAtBefore(Instant now); // for scheduler
}