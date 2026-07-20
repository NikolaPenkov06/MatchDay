package bg.softuni.matchday.email.service;

import bg.softuni.matchday.email.client.EmailClient;
import bg.softuni.matchday.email.client.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EmailService {

    private final EmailClient emailClient;

    @Autowired
    public EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    public void sendEmail(UUID userId, String email, String subject, String body) {
        EmailRequest emailRequest = EmailRequest.builder()
                .userId(userId)
                .userEmail(email)
                .subject(subject)
                .body(body)
                .build();

            ResponseEntity<Void> httpResponse = emailClient.sendEmail(emailRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Email Not Sent");
            }
    }
}
