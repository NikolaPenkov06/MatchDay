package bg.softuni.matchday.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {

    private String title;
    private String image;
    private String content;
    private String author;
}