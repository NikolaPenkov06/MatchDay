package bg.softuni.matchday.comment.service;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.comment.model.Comment;
import bg.softuni.matchday.comment.repository.CommentRepository;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.repository.UserRepository;
import bg.softuni.matchday.user.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleService articleService;
    private final UserService userService;


    @Autowired
    public CommentService(CommentRepository commentRepository, ArticleService articleService, UserService userService) {
        this.commentRepository = commentRepository;
        this.articleService = articleService;
        this.userService = userService;
    }

    public void addComment(UUID id, UUID userId, String content) {
        Article article = articleService.getById(id);
        User user = userService.getById(userId);

        Comment comment = Comment.builder()
                .content(content)
                .article(article)
                .commenter(user)
                .commentDate(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

    }
}
