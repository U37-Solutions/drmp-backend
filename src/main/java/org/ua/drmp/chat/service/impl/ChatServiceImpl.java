package org.ua.drmp.chat.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ua.drmp.chat.dto.ChatDto;
import org.ua.drmp.chat.dto.ChatMessageDto;
import org.ua.drmp.chat.dto.ChatResponse;
import org.ua.drmp.chat.dto.CreateChatRequest;
import org.ua.drmp.chat.entity.Chat;
import org.ua.drmp.chat.entity.ChatMessage;
import org.ua.drmp.chat.repo.ChatMessageRepository;
import org.ua.drmp.chat.repo.ChatRepository;
import org.ua.drmp.chat.service.ChatMessageService;
import org.ua.drmp.chat.service.ChatService;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.company.repo.CompanyRepository;
import org.ua.drmp.entity.User;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.exception.ResourceNotFoundException;
import org.ua.drmp.exception.UserNotFoundException;
import org.ua.drmp.repo.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;
	private final ChatMessageService chatMessageService;
	private final ChatMessageRepository chatMessageRepository;
	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;
	@Override
	public ChatResponse startAnonymousChat(CreateChatRequest request) {
		Company company = companyRepository.findById(request.companyId())
			.orElseThrow(() -> new IllegalArgumentException("Company not found"));

		Chat chat = new Chat();
		chat.setAccessToken(UUID.randomUUID().toString());
		// chat.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));
		chat.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES)); // 30 хв
		chat.setCompany(company);
		chat = chatRepository.save(chat);

		chatMessageService.saveMessage(chat, request.message(), "client");

		return new ChatResponse(chat.getId(), chat.getAccessToken(), chat.getExpiresAt());
	}

	@Override
	public List<ChatMessageDto> getChatHistoryByToken(String token) {
		Chat chat = chatRepository.findByAccessToken(token)
			.orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
		return chatMessageService.getMessages(chat);
	}

	@Override
	public Chat validateChatToken(String token) {
		log.info("Validating token: {}", token);
		Chat chat = chatRepository.findByAccessToken(token)
			.orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

		if (chat.getExpiresAt().isBefore(Instant.now())) {
			throw new BadRequestException("Chat expired");
		}

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
	public List<ChatDto> getActiveChats(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		Long companyId = Optional.ofNullable(user.getCompany())
			.map(Company::getId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not assigned to user"));

		return chatRepository.findActiveChatsByCompanyId(companyId)
			.stream()
			.map(this::mapToDto)
			.toList();
	}

	@Override
	public List<ChatDto> getArchivedChats(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		Long companyId = Optional.ofNullable(user.getCompany())
			.map(Company::getId)
			.orElseThrow(() -> new ResourceNotFoundException("Company not assigned to user"));

		return chatRepository.findTop50ArchivedChatsByCompanyId(companyId)
			.stream()
			.map(this::mapToDto)
			.toList();
	}

	@Override
	public void subscribeToNotifications(Long chatId) {
		Chat chat = chatRepository.findById(chatId)
			.orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
		chat.setNotifyCompanyUser(true);
		chatRepository.save(chat);
	}

	private ChatDto mapToDto(Chat chat) {
		Optional<ChatMessage> lastMsgOpt = chatMessageRepository.findTopByChatOrderBySentAtDesc(chat);

		String lastMessage = lastMsgOpt.map(ChatMessage::getContent).orElse(null);
		return new ChatDto(
			chat.getId(),
			chat.getAccessToken(),
			chat.getCreatedAt(),
			chat.getExpiresAt(),
			chat.getUpdatedAt(),
			chat.isArchived(),
			lastMessage
		);
	}

}
