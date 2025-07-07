package org.ua.drmp.chat.service;

import java.util.List;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.dto.ChatResponse;
import org.ua.drmp.chat.dto.CreateChatRequest;
import org.ua.drmp.chat.entity.Chat;

public interface ChatService {

	ChatResponse startAnonymousChat(CreateChatRequest request);
	List<ChatMessageDto> getChatHistoryByToken(String token);
	Chat validateChatToken(String token);
	void deleteChat(Long chatId);
	List<Chat> getActiveChats();
	List<Chat> getArchivedChats();
}
