package bg.softuni.matchday.email.service;

import bg.softuni.matchday.email.model.Email;
import bg.softuni.matchday.email.reposritory.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final MailSender mailSender;

    @Autowired
    public EmailService(EmailRepository emailRepository, MailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    public void sendEmail(UUID userId, String email, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        Email emailEntity = Email.builder()
                .subject(subject)
                .body(body)
                .createdOn(LocalDateTime.now())
                .userId(userId)
                .build();

        emailRepository.save(emailEntity);
    }
}