package bg.softuni.matchday.team;

import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.repository.TeamRepository;
import bg.softuni.matchday.team.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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
}
