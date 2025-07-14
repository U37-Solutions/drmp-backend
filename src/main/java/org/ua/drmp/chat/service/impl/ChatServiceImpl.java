package org.ua.drmp.chat.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.dto.ChatResponse;
import org.ua.drmp.chat.dto.CreateChatRequest;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.repo.ChatRepository;
import org.ua.drmp.chat.service.ChatMessageService;
import org.ua.drmp.chat.service.ChatService;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;
	private final ChatMessageService chatMessageService;
	private final CompanyRepository companyRepository;
	@Override
	public ChatResponse startAnonymousChat(CreateChatRequest request) {
		Company company = companyRepository.findById(request.companyId())
			.orElseThrow(() -> new IllegalArgumentException("Company not found"));

		Chat chat = new Chat();
		chat.setAccessToken(UUID.randomUUID().toString());
		chat.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));
		chat.setCompany(company);
		chat = chatRepository.save(chat);

		chatMessageService.saveMessage(chat, request.message(), "client");

		return new ChatResponse(chat.getId(), chat.getAccessToken(), chat.getExpiresAt());
	}

	@Override
	public List<ChatMessageDto> getChatHistoryByToken(String token) {
		Chat chat = validateChatToken(token);
		return chatMessageService.getMessages(chat);
	}

	@Override
	public Chat validateChatToken(String token) {
		log.info("Validating token: {}", token);
		Chat chat = chatRepository.findByAccessToken(token)
			.orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

		if (chat.isArchived()) throw new BadRequestException("Chat is archived");
		if (chat.getExpiresAt().isBefore(Instant.now())) throw new BadRequestException("Chat expired");

		return chat;
	}

	@Override
	public void deleteChat(Long chatId) {
		chatRepository.deleteById(chatId);
	}

	@Override
	public void unsubscribeFromNotifications(Long chatId) {
		Chat chat = chatRepository.findById(chatId)
			.orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
		chat.setNotifyCompanyUser(false);
		chatRepository.save(chat);
	}

	@Override
	public List<Chat> getActiveChats() {
		return chatRepository.findByArchivedFalseOrderByUpdatedAtDesc();
	}

	@Override
	public List<Chat> getArchivedChats() {
		return chatRepository.findTop50ByArchivedTrueOrderByExpiresAtDesc();
	}
}
