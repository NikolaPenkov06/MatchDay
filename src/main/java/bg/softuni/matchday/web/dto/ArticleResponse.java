package bg.softuni.matchday.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    private UUID id;
    private String title;
    private String picture;
    private String content;
    private String author;
    private LocalDate publishDate;
    private Collection<CommentResponse> comments;
}
