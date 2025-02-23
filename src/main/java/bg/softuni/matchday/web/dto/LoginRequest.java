package bg.softuni.matchday.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Size(min = 4, message = "Your username should be at least 4 characters long")
    private String username;
    @Size(min = 6, message = "Your username should be at least 6 characters long")
    private String password;
}
