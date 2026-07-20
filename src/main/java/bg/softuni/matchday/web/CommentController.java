package bg.softuni.matchday.web;

import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.comment.service.CommentService;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddCommentRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/articles/{id}/comments")
    public String addComment(@PathVariable UUID id, @Valid AddCommentRequest commentAddRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        if (bindingResult.hasErrors()) {
            return "redirect:/article/" + id;
        }

        commentService.addComment(id, authenticationDetails.getUserId(), commentAddRequest.getContent());

        return "redirect:/article/" + id;
    }
}
