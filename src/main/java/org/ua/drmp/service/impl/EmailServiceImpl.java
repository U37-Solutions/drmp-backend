package org.ua.drmp.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.ua.drmp.service.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private static final String RESET_PASSWORD = "Відновлення пароля";
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${spring.mail.username}")
	private String senderEmail;

	@Override
	public void sendResetPasswordEmail(String email, String token) {
		Context context = new Context();

		String resetUrl = baseUrl + "/reset-password/" + token;
		context.setVariable("resetUrl", resetUrl);
		String htmlContent = templateEngine.process("reset-password.html", context);

		sendHtmlEmail(email, RESET_PASSWORD, htmlContent);
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
			throw new RuntimeException("Failed to send email", e);
		}
	}
}
