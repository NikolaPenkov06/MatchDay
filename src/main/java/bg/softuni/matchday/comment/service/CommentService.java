package bg.softuni.matchday.comment.service;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.comment.CommentClient;
import bg.softuni.matchday.comment.model.Comment;
import bg.softuni.matchday.comment.repository.CommentRepository;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.repository.UserRepository;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddCommentRequest;
import bg.softuni.matchday.web.dto.CommentResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CommentService {

    private final CommentClient commentClient;

    @Autowired
    public CommentService(CommentClient commentClient) {
        this.commentClient = commentClient;
    }

    @CacheEvict(value = "comments", key = "#articleId")
    public void addComment(UUID articleId, UUID userId, String content) {

        AddCommentRequest request = new AddCommentRequest();
        request.setContent(content);
        request.setCommenterId(userId);

        commentClient.addComment(articleId, request);
    }

    @Cacheable(value = "comments", key = "#articleId")
    public List<CommentResponse> getCommentsByArticleId(UUID articleId) {
        return commentClient.getComments(articleId);
    }
}
