package bg.softuni.matchday.web;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.comment.model.Comment;
import bg.softuni.matchday.comment.service.CommentService;
import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.league.service.LeagueService;
import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.Role;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
public class IndexController {
    private final TeamService teamService;
    private final UserService userService;
    private final ArticleService articleService;
    private final GameService gameService;;

    @Autowired
    public IndexController(TeamService teamService, UserService userService, ArticleService articleService, GameService gameService) {
        this.teamService = teamService;
        this.userService = userService;
        this.articleService = articleService;
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String getIndexPage(){

        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session){

        ModelAndView modelAndView = new ModelAndView("login");

        if(bindingResult.hasErrors()){
            return modelAndView;
        }

        User loggedInUser = userService.login(loginRequest);

        if(loggedInUser == null){
            boolean wrongCredentials = true;
            modelAndView.addObject("wrongCredentials", wrongCredentials);
            return modelAndView;
        }

        session.setAttribute("user_id", loggedInUser.getId());

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        modelAndView.addObject("passwordsMatch", true);

        getListOfTeamNames(modelAndView);
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult){

        ModelAndView modelAndView = new ModelAndView("register");

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("registerRequest", registerRequest);

            return checkIfUsernameOrEmailIsInUse(registerRequest, modelAndView, true);

        }

        return checkIfUsernameOrEmailIsInUse(registerRequest, modelAndView, false);

    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        //articleService.addArticle();
        //articleService.extendArticle();
        //teamService.resetStats();
        //gameService.addGame();
        //teamService.updateTeams();
        //teamService.updateManagers();
        //gameService.addAllGames();
        User user = userService.getById(authenticationDetails.getUserId());
        List<Team> teams = user.getFavouriteTeam().getLeague().getTeams();
        teams.sort(Comparator.comparingInt(Team::getPoints).thenComparingInt(team -> team.getGoalsFor() - team.getGoalsAgainst()).reversed());
        League league = user.getFavouriteTeam().getLeague();
        List<Article> articles = articleService.getLimitedArticles();
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("teams", teams);
        modelAndView.addObject("articles", articles);
        modelAndView.addObject("upcomingMatches", gameService.getUpcomingGamesLimited(league));
        modelAndView.addObject("latestMatches", gameService.getLatestGamesLimited(league));
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    public ModelAndView checkIfUsernameOrEmailIsInUse(RegisterRequest registerRequest, ModelAndView modelAndView, boolean isInBindingCheck) {

        modelAndView.addObject("passwordsMatch", userService.doPasswordsMatch(registerRequest));
        getListOfTeamNames(modelAndView);

        switch (userService.checkForTakenCredentials(registerRequest)){

            case "Username":
                modelAndView.addObject("takenUsername", true);
                return modelAndView;

            case "Email":
                modelAndView.addObject("takenEmail", true);
                return modelAndView;

            case "Username Email":
                modelAndView.addObject("takenEmail", true);
                modelAndView.addObject("takenUsername", true);
                return modelAndView;
            default:
                if(isInBindingCheck || !userService.doPasswordsMatch(registerRequest)){
                    return modelAndView;
                }
                userService.register(registerRequest);
                return new ModelAndView("redirect:/login");
        }

    }

    public void getListOfTeamNames(ModelAndView modelAndView) {
        List<String> teamNames = teamService.getAllTeamsNames();
        Collections.sort(teamNames);
        modelAndView.addObject("teamNames", teamNames);
    }


}
