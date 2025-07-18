package org.ua.drmp.chat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.chat.dto.ChatDto;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.dto.ChatResponse;
import org.ua.drmp.chat.dto.CreateChatRequest;
import org.ua.drmp.chat.service.ChatService;
import org.ua.drmp.swagger.annotation.ApiError400;
import org.ua.drmp.swagger.annotation.ApiError404;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@PostMapping("/start")
	public ResponseEntity<ChatResponse> startChat(@RequestBody CreateChatRequest request) {
		return ResponseEntity.ok(chatService.startAnonymousChat(request));
	}

	@ApiError400
	@ApiError404
	@GetMapping("/history")
	public ResponseEntity<List<ChatMessageDto>> getHistory(@RequestHeader("X-Chat-Token") String token) {
		return ResponseEntity.ok(chatService.getChatHistoryByToken(token));
	}

	@DeleteMapping("/{chatId}")
	public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
		chatService.deleteChat(chatId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/active")
	@PreAuthorize("hasAnyRole('COMPANY_ADMIN', 'COMPANY_USER')")
	public ResponseEntity<List<ChatDto>> getActiveChats() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(chatService.getActiveChats(email));
	}

	@GetMapping("/archived")
	@PreAuthorize("hasAnyRole('COMPANY_ADMIN', 'COMPANY_USER')")
	public ResponseEntity<List<ChatDto>> getArchivedChats() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(chatService.getArchivedChats(email));
	}

	@PostMapping("/unsubscribe/{chatId}")
	@PreAuthorize("hasRole('COMPANY_USER')")
	public ResponseEntity<Void> unsubscribe(@PathVariable Long chatId) {
		chatService.unsubscribeFromNotifications(chatId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/subscribe/{chatId}")
	@PreAuthorize("hasRole('COMPANY_USER')")
	public ResponseEntity<Void> subscribe(@PathVariable Long chatId) {
		chatService.subscribeToNotifications(chatId);
		return ResponseEntity.ok().build();
	}
}
