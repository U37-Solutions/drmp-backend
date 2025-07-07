package org.ua.drmp.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.entity.ChatMessage;
import org.ua.drmp.chat.service.ChatMessageService;
import org.ua.drmp.chat.service.ChatService;
import org.ua.drmp.exception.TokenValidationException;
import org.ua.drmp.service.EmailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final ChatService chatService;
	private final ChatMessageService chatMessageService;
	private final EmailService emailService;
	private final ObjectMapper objectMapper;

	private final Map<Long, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		try {
			String token = getTokenFromSession(session);
			Chat chat = chatService.validateChatToken(token);
			sessions.computeIfAbsent(chat.getId(), id -> new ArrayList<>()).add(session);
			log.info("WebSocket connection established: sessionId={}, chatId={}", session.getId(), chat.getId());
		} catch (Exception e) {
			log.error("WebSocket Error during connection: {}", e.getMessage(), e);
			try {
				session.close(CloseStatus.BAD_DATA);
			} catch (IOException ioException) {
				log.error("Error closing session: {}", ioException.getMessage(), ioException);
			}
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			ChatMessageDto dto = parseIncomingMessage(message);
			Chat chat = chatService.validateChatToken(dto.getAccessToken());
			ChatMessage saved = chatMessageService.saveMessage(chat, dto.getContent(), dto.getSenderType());
			ChatMessageDto outgoing = ChatMessage.convertToDto(saved);

			List<WebSocketSession> activeSessions = sessions.get(chat.getId());
			if (activeSessions != null) {
				// видаляємо закриті сесії
				activeSessions.removeIf(s -> !s.isOpen());
				for (WebSocketSession s : activeSessions) {
					if (s.isOpen()) {
						s.sendMessage(new TextMessage(toJson(outgoing)));
					}
				}
			}

			if (!StringUtils.equals(saved.getSenderType(), "admin")) {
				emailService.notifyAdmin(saved);
			}
		} catch (Exception e) {
			log.error("Error handling WebSocket message: {}", e.getMessage(), e);
			if (session.isOpen()) session.close(CloseStatus.SERVER_ERROR);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		log.info("Session closed: sessionId={}, reason={}", session.getId(), status);
		// Видалити сесію з усіх списків
		sessions.values().forEach(list -> list.remove(session));
	}

	private String getTokenFromSession(WebSocketSession session) {
		URI uri = session.getUri();
		log.info("URI = {}", uri);
		if (uri == null || uri.getQuery() == null) throw new TokenValidationException("Missing token");
		return Arrays.stream(uri.getQuery().split("&"))
			.filter(p -> p.startsWith("token="))
			.map(p -> p.substring("token=".length()))
			.findFirst()
			.orElseThrow(() -> new TokenValidationException("Missing token"));
	}

	private ChatMessageDto parseIncomingMessage(TextMessage message) throws IOException {
		return objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
	}

	private String toJson(ChatMessageDto dto) throws IOException {
		return objectMapper.writeValueAsString(dto);
	}
}


