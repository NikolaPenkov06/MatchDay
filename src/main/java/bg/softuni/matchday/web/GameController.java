package bg.softuni.matchday.web;

import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.league.service.LeagueService;
import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddMatchRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
public class GameController {
    private final TeamService teamService;
    private final UserService userService;
    private final GameService gameService;
    private final LeagueService leagueService;

    @Autowired
    public GameController(TeamService teamService, UserService userService, GameService gameService, LeagueService leagueService) {
        this.teamService = teamService;
        this.userService = userService;
        this.gameService = gameService;
        this.leagueService = leagueService;
    }

    @GetMapping("/game/{id}")
    public ModelAndView getGamePage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        Game game = gameService.getById(id);
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        ModelAndView modelAndView = new ModelAndView("game");
        modelAndView.addObject("user", user);
        modelAndView.addObject("team", user.getFavouriteTeam());
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("game", game);

        return modelAndView;
    }

    @GetMapping("/matches-menu")
    public ModelAndView getMatchesMenuPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        User user = userService.getById(authenticationDetails.getUserId());
        League serieA = leagueService.findByName("Serie A");
        League laLiga = leagueService.findByName("La Liga");
        League ligue1 = leagueService.findByName("Ligue 1");
        League prem = leagueService.findByName("Premier League");
        League bundesliga = leagueService.findByName("Bundesliga");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("matches-menu");
        modelAndView.addObject("user", user);
        modelAndView.addObject("serieA", serieA);
        modelAndView.addObject("laLiga", laLiga);
        modelAndView.addObject("ligue1", ligue1);
        modelAndView.addObject("prem", prem);
        modelAndView.addObject("bundesliga", bundesliga);
        return modelAndView;
    }

    @GetMapping("/matches/{id}")
    public ModelAndView getMatchesPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        League league = leagueService.findById(id);
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("matches");
        modelAndView.addObject("user", user);
        modelAndView.addObject("upcomingMatches", gameService.getUpcomingGamesLimited(league));
        modelAndView.addObject("latestMatches", gameService.getLatestGamesLimited(league));
        modelAndView.addObject("league",league);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    @GetMapping("/all-upcoming-matches/league/{id}")
    public ModelAndView getAllUpcomingLeagueMatchesPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        League league = leagueService.findById(id);
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        String header = leagueService.getHeaderUpcoming(league);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("all-matches");
        modelAndView.addObject("user", user);
        modelAndView.addObject("matches", gameService.getAllUpcomingGames(league));
        modelAndView.addObject("header",header);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    @GetMapping("/all-latest-matches/league/{id}")
    public ModelAndView getAllLatestLeagueMatchesPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        League league = leagueService.findById(id);
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        String header = leagueService.getHeaderLatest(league);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("all-matches");
        modelAndView.addObject("user", user);
        modelAndView.addObject("matches", gameService.getAllLatestGames(league));
        modelAndView.addObject("header",header);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    @GetMapping("/all-upcoming-matches/team/{id}")
    public ModelAndView getAllUpcomingTeamMatchesPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        Team team = teamService.getById(id);
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        String header = teamService.getHeaderUpcoming(team);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("all-matches");
        modelAndView.addObject("user", user);
        modelAndView.addObject("matches", gameService.getAllUpcomingMatches(team));
        modelAndView.addObject("header",header);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    @GetMapping("/all-latest-matches/team/{id}")
    public ModelAndView getAllLatestTeamMatchesPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails){
        Team team = teamService.getById(id);
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        String header = teamService.getHeaderLatest(team);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("all-matches");
        modelAndView.addObject("user", user);
        modelAndView.addObject("matches", gameService.getAllLatestMatches(team));
        modelAndView.addObject("header",header);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        return modelAndView;
    }

    @GetMapping("/add-match")
    public ModelAndView getAddMatchPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails, @RequestParam(required = false) UUID leagueId) {
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        List<League> leagues = leagueService.getAllLeagues();
        leagues.sort(Comparator.comparing(League::getName));
        if (leagueId == null) {
            leagueId = leagues.get(0).getId();
        }

        League selectedLeague = leagueService.findById(leagueId);
        List<Team> teams = selectedLeague.getTeams();
        teams.sort(Comparator.comparing(Team::getName));

        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setLeagueId(leagueId);

        ModelAndView modelAndView = new ModelAndView("add-match");
        getAddMatchObjects(user, position, leagues, selectedLeague, teams, addMatchRequest, modelAndView);

        return modelAndView;
    }

    private void getAddMatchObjects(User user, String position, List<League> leagues, League selectedLeague, List<Team> teams, AddMatchRequest addMatchRequest, ModelAndView modelAndView) {
        modelAndView.addObject("user", user);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("leagues", leagues);
        modelAndView.addObject("logo", selectedLeague.getLogo());
        modelAndView.addObject("teams", teams);
        modelAndView.addObject("addMatchRequest", addMatchRequest);
    }

    @PostMapping("/add-match")
    public ModelAndView addMatch(@Valid AddMatchRequest addMatchRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails, RedirectAttributes redirectAttributes){

        ModelAndView modelAndView = new ModelAndView("add-match");

        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());

        List<League> leagues = leagueService.getAllLeagues();
        leagues.sort(Comparator.comparing(League::getName));
        League selectedLeague = leagueService.findById(addMatchRequest.getLeagueId());
        List<Team> teams = selectedLeague.getTeams();
        teams.sort(Comparator.comparing(Team::getName));

        getAddMatchObjects(user, position, leagues, selectedLeague, teams, addMatchRequest, modelAndView);

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        if(addMatchRequest.getHomeTeamId().equals(addMatchRequest.getAwayTeamId())){
            modelAndView.addObject("sameTeams", true);
            return modelAndView;
        }

        if(!gameService.checkIfDateIsValid(addMatchRequest)){
            modelAndView.addObject("invalidDate", true);
            return modelAndView;
        }

        if(!gameService.checkIfTimeIsValid(addMatchRequest)){
            modelAndView.addObject("invalidTime", true);
            return modelAndView;
        }

        if(!gameService.checkIfDateIsInSeason(addMatchRequest)){
            modelAndView.addObject("dateOutOfSeason", true);
            return modelAndView;
        }

        gameService.addGame(addMatchRequest);

        redirectAttributes.addFlashAttribute("matchAdded", true);

        return new ModelAndView("redirect:/add-match");

    }
}
