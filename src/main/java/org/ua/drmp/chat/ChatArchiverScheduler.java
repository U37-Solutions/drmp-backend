package org.ua.drmp.chat;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.repo.ChatRepository;

@Component
@RequiredArgsConstructor
public class ChatArchiverScheduler {
	private final ChatRepository chatRepository;

	//@Scheduled(cron = "0 0 * * * *") // кожну годину
	@Scheduled(cron = "0 */5 * * * *") // кожні 5 хвилин
	public void archiveExpiredChats() {
		List<Chat> expired = chatRepository.findByArchivedFalseAndExpiresAtBefore(Instant.now());
		for (Chat chat : expired) {
			chat.setArchived(true);
		}
		chatRepository.saveAll(expired);
	}
}