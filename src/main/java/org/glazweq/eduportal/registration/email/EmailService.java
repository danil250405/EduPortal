package org.glazweq.eduportal.registration.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
//@AllArgsConstructor
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;



    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom(fromEmail);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
