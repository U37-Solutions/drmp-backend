package org.ua.drmp.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.ua.drmp.chat.entity.ChatMessage;
import org.ua.drmp.company.entity.Company;
import org.ua.drmp.exception.BadRequestException;
import org.ua.drmp.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	private static final String RESET_PASSWORD = "Відновлення пароля";
	private static final String INVITE_SUBJECT = "Запрошення до платформи";
	private static final String SIGNUP_SUCCESS = "Реєстрація успішна";
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${app.admin.email}")
	private String senderEmail;

	@Override
	public void sendResetPasswordEmail(String email, String token) {
		Context context = new Context();

		String resetUrl = baseUrl + "/reset-password/" + token;
		context.setVariable("resetUrl", resetUrl);
		String htmlContent = templateEngine.process("reset-password.html", context);

		sendHtmlEmail(email, RESET_PASSWORD, htmlContent);
	}

	@Override
	public void sendInviteUserEmail(String email, String token) {
		Context context = new Context();
		String acceptUrl = baseUrl + "/sign-up/" + token;
		context.setVariable("acceptUrl", acceptUrl);
		String htmlContent = templateEngine.process("accept-invite.html", context);

		sendHtmlEmail(email, INVITE_SUBJECT, htmlContent);
	}

	@Override
	public void sendSuccessfulRegistrationEmail(String email) {
		Context context = new Context();
		context.setVariable("loginUrl", baseUrl + "/login");
		String htmlContent = templateEngine.process("signup-success.html", context);
		sendHtmlEmail(email, SIGNUP_SUCCESS, htmlContent);
	}

	@Override
	public void notifyAdmin(ChatMessage message) {
		if (!message.getChat().isNotifyCompanyUser()) {
			log.info("Notifications disabled for chat {}", message.getChat().getId());
			return;
		}
		Context context = new Context();
		context.setVariable("messageContent", message.getContent());
		context.setVariable("sentAt", message.getSentAt().toString());
		context.setVariable("link", baseUrl + "/chats?chatId=" + message.getChat().getId());

		String htmlContent = templateEngine.process("new-message.html", context);
		sendHtmlEmail(message.getChat().getCompany().getEmail(), "Нове повідомлення в чаті", htmlContent);
	}

	@Override
	public void sendCompanyRegistrationNotification(Company company) {
		Context context = new Context();
		context.setVariable("companyName", company.getName());
		context.setVariable("reviewLink", baseUrl + "/admin/companies/" + company.getId());
		String htmlContent = templateEngine.process("company-review-notify.html", context);
		sendHtmlEmail(senderEmail, "Нова компанія чекає підтвердження", htmlContent);
	}

	@Override
	public void sendAccountCredentialsEmail(String to, String rawPassword) {
		Context context = new Context();
		context.setVariable("email", to);
		context.setVariable("password", rawPassword);
		context.setVariable("loginUrl", baseUrl + "/login");
		String html = templateEngine.process("company-approved.html", context);
		sendHtmlEmail(to, "Реєстрацію підтверджено", html);
	}

	@Override
	public void sendCompanyRejectionEmail(String to, String companyName, String reason, String contactEmail) {
		Context context = new Context();
		context.setVariable("companyName", companyName);
		context.setVariable("reason", reason);
		context.setVariable("contactEmail", contactEmail);
		String html = templateEngine.process("company-rejected.html", context);
		sendHtmlEmail(to, "Реєстрацію компанії відхилено", html);
	}

	@Override
	public void sendInviteForCompanyUser(String email, String password) {
		Context context = new Context();
		context.setVariable("email", email);
		context.setVariable("password", password);
		context.setVariable("loginUrl", baseUrl + "/login");
		String html = templateEngine.process("invite-company-user.html", context);
		sendHtmlEmail(email, INVITE_SUBJECT, html);
	}

	private void sendHtmlEmail(String to, String subject, String htmlContent) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setText(htmlContent, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom(senderEmail);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new BadRequestException("Failed to send email");
		}
	}
}
