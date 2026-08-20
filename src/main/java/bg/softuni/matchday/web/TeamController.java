package bg.softuni.matchday.web;

import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.service.LeagueService;
import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping
public class TeamController {
    private final TeamService teamService;
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public TeamController(TeamService teamService, UserService userService, GameService gameService) {
        this.teamService = teamService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/team/{id}")
    public ModelAndView getTeamInfoPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        Team team = teamService.getById(id);
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        String shownTeamPosition = teamService.getTeamPosition(team);
        ModelAndView modelAndView = new ModelAndView("team-info");
        modelAndView.addObject("user", user);
        modelAndView.addObject("team", user.getFavouriteTeam());
        modelAndView.addObject("shownTeam", team);
        modelAndView.addObject("upcomingMatches", gameService.getUpcomingMatchesLimited(team));
        modelAndView.addObject("latestMatches", gameService.getLatestMatchesLimited(team));
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("shownTeamPosition", shownTeamPosition);
        modelAndView.addObject("shownTeamLatestMatch", teamService.getLastMatchDetails(team));

        return modelAndView;
    }
}
