package org.ua.drmp.chat.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.entity.ChatMessage;
import org.ua.drmp.chat.repo.ChatMessageRepository;
import org.ua.drmp.chat.service.ChatMessageService;

@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService {
	private final ChatMessageRepository chatMessageRepository;

	@Override
	public ChatMessage saveMessage(Chat chat, String content, String senderType) {
		ChatMessage msg = new ChatMessage();
		msg.setChat(chat);
		msg.setContent(content);
		msg.setSenderType(senderType);
		msg.setSentAt(Instant.now());
		chat.setUpdatedAt(Instant.now());
		return chatMessageRepository.save(msg);
	}

	@Override
	public List<ChatMessageDto> getMessages(Chat chat) {
		return chatMessageRepository.findByChatOrderBySentAtAsc(chat).stream()
			.map(ChatMessage::convertToDto)
			.collect(Collectors.toList());
	}
}
