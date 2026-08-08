package bg.softuni.matchday.email;

import bg.softuni.matchday.email.client.EmailClient;
import bg.softuni.matchday.email.client.dto.EmailRequest;
import bg.softuni.matchday.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUTest {
    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_sendsCorrectEmail() {
        UUID userId = UUID.randomUUID();

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);

        when(emailClient.sendEmail(any(EmailRequest.class)))
                .thenReturn(response);

        emailService.sendEmail(
                userId,
                "test@gmail.com",
                "Subject",
                "Hello!"
        );

        ArgumentCaptor<EmailRequest> captor =
                ArgumentCaptor.forClass(EmailRequest.class);

        verify(emailClient).sendEmail(captor.capture());

        EmailRequest request = captor.getValue();

        assertEquals(userId, request.getUserId());
        assertEquals("test@gmail.com", request.getUserEmail());
        assertEquals("Subject", request.getSubject());
        assertEquals("Hello!", request.getBody());

    }
}
