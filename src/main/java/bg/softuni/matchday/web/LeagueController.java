package bg.softuni.matchday.web;

import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.model.League;
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

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
public class LeagueController {
    private final TeamService teamService;
    private final UserService userService;
    private final GameService gameService;
    private final LeagueService leagueService;

    @Autowired
    public LeagueController(TeamService teamService, UserService userService, GameService gameService, LeagueService leagueService) {
        this.teamService = teamService;
        this.userService = userService;
        this.gameService = gameService;
        this.leagueService = leagueService;
    }

    @GetMapping("/league-tables-menu")
    public ModelAndView getLeagueTablesPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        User user = userService.getById(authenticationDetails.getUserId());
        League serieA = leagueService.findByName("Serie A");
        League laLiga = leagueService.findByName("La Liga");
        League ligue1 = leagueService.findByName("Ligue 1");
        League prem = leagueService.findByName("Premier League");
        League bundesliga = leagueService.findByName("Bundesliga");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("league-tables-menu");
        modelAndView.addObject("user", user);
        modelAndView.addObject("serieA", serieA);
        modelAndView.addObject("laLiga", laLiga);
        modelAndView.addObject("ligue1", ligue1);
        modelAndView.addObject("prem", prem);
        modelAndView.addObject("bundesliga", bundesliga);
        return modelAndView;
    }

    @GetMapping("/league-table/{id}")
    public ModelAndView getLeagueTablePage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        List<Team> teams = leagueService.findById(id).getTeams();
        teams.sort(Comparator.comparingInt(Team::getPoints).thenComparingInt(team -> team.getGoalsFor() - team.getGoalsAgainst()).reversed());
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("league-table");
        modelAndView.addObject("user", user);
        modelAndView.addObject("teams", teams);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }
}
