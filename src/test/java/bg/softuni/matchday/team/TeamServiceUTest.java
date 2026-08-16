package bg.softuni.matchday.team;

import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.repository.TeamRepository;
import bg.softuni.matchday.team.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamServiceUTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void getAllTeamNames_returnsAllTeamNames() {
        Team team1 = new Team();
        team1.setName("Team 1");

        Team team2 = new Team();
        team2.setName("Team 2");

        Team team3 = new Team();
        team3.setName("Team 3");

        when(teamRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(team1, team2, team3)));

        List<String> result = teamService.getAllTeamsNames();

        verify(teamRepository).findAll();

        assertEquals(List.of("Team 1", "Team 2", "Team 3"), result);
    }

    @Test
    void getTeamPosition_returnsCorrectPosition() {
        League league = new League();

        Team team1 = new Team();
        team1.setName("Team 1");
        team1.setPoints(30);
        team1.setGoalsFor(40);
        team1.setGoalsAgainst(10);

        Team team2 = new Team();
        team2.setName("Team 2");
        team2.setPoints(25);
        team2.setGoalsFor(35);
        team2.setGoalsAgainst(15);

        Team team3 = new Team();
        team3.setName("Team 3");
        team3.setPoints(20);
        team3.setGoalsFor(30);
        team3.setGoalsAgainst(20);

        Team team4 = new Team();
        team4.setName("Team 4");
        team4.setPoints(15);
        team4.setGoalsFor(20);
        team4.setGoalsAgainst(20);

        league.setTeams(new ArrayList<>(List.of(
                team1, team2, team3, team4
        )));

        team1.setLeague(league);
        team2.setLeague(league);
        team3.setLeague(league);
        team4.setLeague(league);

        assertEquals("1st", teamService.getTeamPosition(team1));
        assertEquals("2nd", teamService.getTeamPosition(team2));
        assertEquals("3rd", teamService.getTeamPosition(team3));
        assertEquals("4th", teamService.getTeamPosition(team4));
    }

    @Test
    void getLastMatchDetails_noGames_returnsNoGamesPlayedYet() {
        Team team = new Team();
        team.setName("Arsenal");
        team.setHomeGames(new ArrayList<>());
        team.setAwayGames(new ArrayList<>());

        String result = teamService.getLastMatchDetails(team);

        assertEquals("No games played yet", result);
    }

    @Test
    void getLastMatchDetails_homeWin_returnsCorrectDetails() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team norwich = new Team();
        norwich.setName("Norwich");

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(norwich);
        game.setHomeGoals(2);
        game.setAwayGoals(0);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        arsenal.setHomeGames(new ArrayList<>(List.of(game)));
        arsenal.setAwayGames(new ArrayList<>());

        String result = teamService.getLastMatchDetails(arsenal);

        assertEquals("2-0 win vs Norwich", result);
    }

    @Test
    void getLastMatchDetails_homeLoss_returnsCorrectDetails() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team norwich = new Team();
        norwich.setName("Norwich");

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(norwich);
        game.setHomeGoals(0);
        game.setAwayGoals(2);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        arsenal.setHomeGames(new ArrayList<>(List.of(game)));
        arsenal.setAwayGames(new ArrayList<>());

        String result = teamService.getLastMatchDetails(arsenal);

        assertEquals("0-2 loss vs Norwich", result);
    }

    @Test
    void getLastMatchDetails_draw_returnsCorrectDetails() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team norwich = new Team();
        norwich.setName("Norwich");

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(norwich);
        game.setHomeGoals(1);
        game.setAwayGoals(1);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        arsenal.setHomeGames(new ArrayList<>(List.of(game)));
        arsenal.setAwayGames(new ArrayList<>());

        String result = teamService.getLastMatchDetails(arsenal);

        assertEquals("1-1 draw vs Norwich", result);
    }

    @Test
    void getLastMatchDetails_awayWin_returnsCorrectDetails() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team norwich = new Team();
        norwich.setName("Norwich");

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(norwich);
        game.setHomeGoals(0);
        game.setAwayGoals(2);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        norwich.setHomeGames(new ArrayList<>());
        norwich.setAwayGames(new ArrayList<>(List.of(game)));

        String result = teamService.getLastMatchDetails(norwich);

        assertEquals("0-2 win vs Arsenal", result);
    }

    @Test
    void getLastMatchDetails_awayLoss_returnsCorrectDetails() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team norwich = new Team();
        norwich.setName("Norwich");

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(norwich);
        game.setHomeGoals(2);
        game.setAwayGoals(0);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        norwich.setHomeGames(new ArrayList<>());
        norwich.setAwayGames(new ArrayList<>(List.of(game)));

        String result = teamService.getLastMatchDetails(norwich);

        assertEquals("2-0 loss vs Arsenal", result);
    }

    @Test
    void getLastMatchDetails_awayDraw_returnsCorrectDetails() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team norwich = new Team();
        norwich.setName("Norwich");

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(norwich);
        game.setHomeGoals(2);
        game.setAwayGoals(2);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        norwich.setHomeGames(new ArrayList<>());
        norwich.setAwayGames(new ArrayList<>(List.of(game)));

        String result = teamService.getLastMatchDetails(norwich);

        assertEquals("2-2 draw vs Arsenal", result);
    }

    @Test
    void findByName_returnsTeam() {
        Team team = new Team();
        team.setName("Arsenal");

        when(teamRepository.findByName("Arsenal"))
                .thenReturn(Optional.of(team));

        Team result = teamService.findByName("Arsenal");

        verify(teamRepository).findByName("Arsenal");

        assertEquals(team, result);
    }

}
