package bg.softuni.matchday.web.dto;

import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.user.model.Country;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min = 4, message = "Your username should be at least 4 characters long")
    private String username;
    @Size(min = 6, message = "Your password should be at least 6 characters long")
    private String password;
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
    @NotBlank(message = "Please provide an email")
    @Email(message = "Please provide a valid email")
    private String email;

    private String firstName;

    private String lastName;
    @NotNull(message = "Please select a country")
    private Country country;
    @NotNull(message = "Please select your favourite team")
    private Team favouriteTeam;
    @URL(message = "Please provide a valid URL")
    private String profilePicture;


}
