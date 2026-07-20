package bg.softuni.matchday.email.client;

import bg.softuni.matchday.email.client.dto.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "email-svc", url = "http://localhost:8081/api/v1/emails")
public interface EmailClient {

    @PostMapping
    ResponseEntity<Void> sendEmail(@RequestBody EmailRequest emailRequest);

}
