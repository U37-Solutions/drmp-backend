package org.ua.drmp.chat.websocket;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.ua.drmp.chat.service.ChatService;
import org.ua.drmp.exception.TokenValidationException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatTokenHandshakeInterceptor implements HandshakeInterceptor {

	private final ChatService chatService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) {
		try {
			String token = extractTokenFromRequest(request);
			log.info("Incoming WebSocket handshake. URI: {}, Token: {}", request.getURI(), token);

			chatService.validateChatToken(token);

			log.info("Chat token validated successfully: {}", token);
			return true;
		} catch (Exception e) {
			log.error("WebSocket handshake failed: {}", e.getMessage(), e);
			return false; // блокуємо хендшейк
		}
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
	}

	private String extractTokenFromRequest(ServerHttpRequest request) {
		URI uri = request.getURI();
		String query = uri.getQuery();
		log.debug("Extracting token from URI: {}", uri);

		if (query == null) throw new TokenValidationException("Missing query string in URI");

		return Arrays.stream(query.split("&"))
			.filter(p -> p.startsWith("token="))
			.map(p -> p.substring("token=".length()))
			.findFirst()
			.orElseThrow(() -> new TokenValidationException("Missing token parameter"));
	}
}

