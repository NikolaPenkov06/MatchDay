package bg.softuni.matchday.email.reposritory;

import bg.softuni.matchday.email.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.Notification;
import java.util.UUID;

@Repository
public interface EmailRepository extends JpaRepository<Email, UUID> {
}
