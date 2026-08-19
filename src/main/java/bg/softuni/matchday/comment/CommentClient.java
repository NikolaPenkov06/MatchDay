package bg.softuni.matchday.comment;

import bg.softuni.matchday.web.dto.AddCommentRequest;
import bg.softuni.matchday.web.dto.CommentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "matchday-service-comments", url = "http://localhost:8081/api/articles")
public interface CommentClient {

    @PostMapping("/{articleId}/comments")
    void addComment(@PathVariable UUID articleId, @RequestBody AddCommentRequest request);

    @GetMapping("/{articleId}/comments")
    List<CommentResponse> getComments(@PathVariable UUID articleId);
}
