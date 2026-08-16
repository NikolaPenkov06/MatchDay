package bg.softuni.matchday.email.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 1000)
    private String body;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private UUID userId;
}
