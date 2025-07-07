package org.ua.drmp.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.ua.drmp.chat.dto.ChatMessageDto;

@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Chat chat;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private String senderType; // "client" або "admin"

	@Column(nullable = false)
	private Instant sentAt = Instant.now();

	public static ChatMessageDto convertToDto(ChatMessage message) {
		ChatMessageDto dto = new ChatMessageDto();
		dto.setChatId(message.getChat().getId());
		dto.setContent(message.getContent());
		dto.setSenderType(message.getSenderType());
		dto.setSentAt(message.getSentAt());
		dto.setAccessToken(message.getChat().getAccessToken());
		return dto;
	}
}
