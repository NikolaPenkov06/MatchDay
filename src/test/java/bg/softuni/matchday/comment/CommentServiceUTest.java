package bg.softuni.matchday.comment;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.comment.model.Comment;
import bg.softuni.matchday.comment.repository.CommentRepository;
import bg.softuni.matchday.comment.service.CommentService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddArticleRequest;
import bg.softuni.matchday.web.dto.AddCommentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleService articleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_addsCorrectComment() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Article article = new Article();
        article.setId(id);

        User user = new User();
        user.setId(userId);

        when(articleService.getById(id))
                .thenReturn(article);

        when(userService.getById(userId))
                .thenReturn(user);

        commentService.addComment(id, userId, "This is a comment");

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(captor.capture());

        Comment savedComment = captor.getValue();

        assertEquals("This is a comment", savedComment.getContent());
        assertEquals(article, savedComment.getArticle());
        assertEquals(user, savedComment.getCommenter());
        assertEquals(LocalDate.now(), savedComment.getCommentDate().toLocalDate());
    }
}
