package bg.softuni.matchday.comment.model;

import jakarta.persistence.*;
import lombok.*;
import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User commenter;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    private Article article;

    @Column(nullable = false)
    private LocalDateTime commentDate;
}
