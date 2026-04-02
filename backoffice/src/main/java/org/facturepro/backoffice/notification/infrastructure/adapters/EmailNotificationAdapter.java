package org.facturepro.backoffice.notification.infrastructure.adapters;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Adaptateur email — isolation de Spring Mail dans l'infrastructure.
 * Probabilité de changer de provider ~70% → isolé ici.
 */
@Component
public class EmailNotificationAdapter {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationAdapter.class);

    private final JavaMailSender mailSender;

    public EmailNotificationAdapter(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(final String to, final String subject, final String htmlBody) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("noreply@facturepro.africa");
            mailSender.send(message);
            log.info("Email envoyé à: {}", to);
        } catch (MessagingException e) {
            log.error("Échec envoi email à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec envoi email", e);
        }
    }
}
