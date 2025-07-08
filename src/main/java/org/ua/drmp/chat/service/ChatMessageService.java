package org.ua.drmp.chat.service;

import java.util.List;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.entity.ChatMessage;

public interface ChatMessageService {
	ChatMessage saveMessage(Chat chat, String content, String senderType);
	List<ChatMessageDto> getMessages(Chat chat);
}

