package bg.softuni.matchday.web;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.comment.model.Comment;
import bg.softuni.matchday.comment.service.CommentService;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.service.LeagueService;
import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddArticleRequest;
import bg.softuni.matchday.web.dto.AddCommentRequest;
import bg.softuni.matchday.web.dto.CommentResponse;
import bg.softuni.matchday.web.dto.CommentView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
public class ArticleController {
    private final TeamService teamService;
    private final UserService userService;
    private final ArticleService articleService;
    private final GameService gameService;
    private final CommentService commentService;

    @Autowired
    public ArticleController(TeamService teamService, UserService userService, ArticleService articleService, GameService gameService, CommentService commentService) {
        this.teamService = teamService;
        this.userService = userService;
        this.articleService = articleService;
        this.gameService = gameService;
        this.commentService = commentService;
    }

    @GetMapping("/articles")
    public ModelAndView getArticlesPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        //articleService.extendArticle();
        //teamService.resetStats();
        //gameService.addGame();
        User user = userService.getById(authenticationDetails.getUserId());
        List<Article> articles = articleService.getAllArticles();
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("articles");
        modelAndView.addObject("user", user);
        modelAndView.addObject("articles", articles);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    @GetMapping("/article/{id}")
    public ModelAndView getArticlePage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        Article article = articleService.getById(id);
        List<CommentView> comments = commentService.getCommentsByArticleId(id).stream().map(comment -> new CommentView(comment.getId(), userService.getById(comment.getCommenterId()), comment.getContent(), comment.getCommentDate())).sorted(Comparator.comparing(CommentView::getCommentDate).reversed()).toList();
        String position = teamService.getTeamPosition(user.getFavouriteTeam());

        ModelAndView modelAndView = new ModelAndView("article");
        modelAndView.addObject("user", user);
        modelAndView.addObject("article", article);
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("addCommentRequest", new AddCommentRequest());

        return modelAndView;
    }

    @GetMapping("/add-article")
    public ModelAndView getAddArticlePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        ModelAndView modelAndView = new ModelAndView("add-article");
        modelAndView.addObject("user", user);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("addArticleRequest", new AddArticleRequest());

        return modelAndView;
    }

    @PostMapping("/add-article")
    public ModelAndView addArticle(@Valid AddArticleRequest addArticleRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails, RedirectAttributes redirectAttributes){
        ModelAndView modelAndView = new ModelAndView("add-article");

        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());

        modelAndView.addObject("user", user);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("user", user);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("addArticleRequest", new AddArticleRequest());

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        articleService.addArticle(addArticleRequest, user);

        redirectAttributes.addFlashAttribute("articleAdded", true);

        return new ModelAndView("redirect:/add-article");

    }

    @DeleteMapping("/article/{id}")
    public ModelAndView deleteArticle(@PathVariable UUID id){

        articleService.deleteArticle(id);

        return new ModelAndView("redirect:/articles");

    }
}
