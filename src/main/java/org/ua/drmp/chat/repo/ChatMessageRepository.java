package org.ua.drmp.chat.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findByChatOrderBySentAtAsc(Chat chat);
	Optional<ChatMessage> findTopByChatOrderBySentAtDesc(Chat chat);
}
