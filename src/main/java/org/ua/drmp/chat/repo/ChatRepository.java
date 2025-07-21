package org.ua.drmp.chat.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ua.drmp.chat.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	Optional<Chat> findByAccessToken(String accessToken);
	@Query("SELECT c FROM Chat c WHERE c.company.id = :companyId AND c.archived = false")
	List<Chat> findActiveChatsByCompanyId(@Param("companyId") Long companyId);

	@Query("SELECT c FROM Chat c WHERE c.company.id = :companyId AND c.archived = true ORDER BY c.expiresAt DESC")
	List<Chat> findTop50ArchivedChatsByCompanyId(@Param("companyId") Long companyId);

	List<Chat> findByArchivedFalseAndExpiresAtBefore(Instant now); // for scheduler
}