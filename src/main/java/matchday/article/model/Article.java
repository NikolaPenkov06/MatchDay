package matchday.article.model;

import jakarta.persistence.*;
import lombok.*;
import matchday.comment.model.Comment;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String picture;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDate publishDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "article")
    private Collection<Comment> comments;
}
