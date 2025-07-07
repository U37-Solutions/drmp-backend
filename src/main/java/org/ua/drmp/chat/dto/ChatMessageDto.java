package org.ua.drmp.chat.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
	private Long chatId;
	private String content;
	private String senderType;
	private Instant sentAt;
	private String accessToken;
}
