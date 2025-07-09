package org.ua.drmp.chat.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.chat.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	Optional<Chat> findByAccessToken(String accessToken);
	List<Chat> findByArchivedFalseOrderByUpdatedAtDesc();
	List<Chat> findTop50ByArchivedTrueOrderByExpiresAtDesc();
	List<Chat> findByArchivedFalseAndExpiresAtBefore(Instant now); // for scheduler
}