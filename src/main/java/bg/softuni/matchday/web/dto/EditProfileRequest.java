package bg.softuni.matchday.web.dto;

import bg.softuni.matchday.user.model.Country;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Builder
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class EditProfileRequest {

    private String firstName;

    private String lastName;
    @Size(min = 4, message = "Username at least 4 symbols long")
    private String username;
    @NotBlank(message = "Please provide an email")
    @Email(message = "Please provide a valid email")
    private String email;
    @NotNull(message = "Select a country")
    private Country country;
    @NotNull(message = "Select your favourite team")
    private String favouriteTeamName;
    @URL(message = "Provide a valid URL or leave blank")
    private String profilePicture;

}
