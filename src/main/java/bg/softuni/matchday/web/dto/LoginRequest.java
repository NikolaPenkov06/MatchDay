package bg.softuni.matchday.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Size(min = 4, message = "Username at least 4 symbols long")
    private String username;
    @Size(min = 6, message = "Password at least 6 symbols long")
    private String password;
}
