package bg.softuni.matchday.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddArticleRequest {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 70, message = "Title is too long")
    private String title;

    @URL(message = "Provide a valid URL")
    @NotBlank(message = "Provide a valid URL")
    private String image;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 10000, message = "Content is too long")
    private String content;
}
