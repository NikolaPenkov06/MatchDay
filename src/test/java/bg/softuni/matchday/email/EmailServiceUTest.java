package bg.softuni.matchday.email;

import bg.softuni.matchday.email.model.Email;
import bg.softuni.matchday.email.reposritory.EmailRepository;
import bg.softuni.matchday.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_sendsCorrectEmail() {
        UUID userId = UUID.randomUUID();

        emailService.sendEmail(
                userId,
                "test@gmail.com",
                "Subject",
                "Hello!"
        );

        // Verify email was sent
        ArgumentCaptor<SimpleMailMessage> messageCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();

        assertEquals("test@gmail.com", message.getTo()[0]);
        assertEquals("Subject", message.getSubject());
        assertEquals("Hello!", message.getText());

        // Verify email was saved to database
        ArgumentCaptor<Email> emailCaptor =
                ArgumentCaptor.forClass(Email.class);

        verify(emailRepository).save(emailCaptor.capture());

        Email email = emailCaptor.getValue();

        assertEquals(userId, email.getUserId());
        assertEquals("Subject", email.getSubject());
        assertEquals("Hello!", email.getBody());
    }
}
