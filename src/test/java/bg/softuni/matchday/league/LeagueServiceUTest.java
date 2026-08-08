package bg.softuni.matchday.league;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.league.repository.LeagueRepository;
import bg.softuni.matchday.league.service.LeagueService;
import bg.softuni.matchday.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeagueServiceUTest {
    @Mock
    private LeagueRepository leagueRepository;

    @InjectMocks
    private LeagueService leagueService;

    @Test
    void getAllLeagues_returnsAllLeagues(){
        League league1 = new League();
        League league2 = new League();
        League league3 = new League();


        when(leagueRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(league1, league2, league3)));

        List<League> result = leagueService.getAllLeagues();

        verify(leagueRepository).findAll();

        assertEquals(result, leagueService.getAllLeagues());
    }

    @Test
    void findByName_returnsRightUser(){

        League league = new League();
        league.setName("Bundesliga");

        when(leagueRepository.findByName("Bundesliga"))
                .thenReturn(Optional.of(league));

        League result = leagueService.findByName("Bundesliga");

        assertEquals(league, result);

    }

    @Test
    void findById_returnsRightLeague(){
        UUID uuid = UUID.randomUUID();

        League league = new League();
        league.setId(uuid);

        when(leagueRepository.findById(uuid))
                .thenReturn(Optional.of(league));

        League result = leagueService.findById(uuid);

        assertEquals(league, result);

    }

    @Test
    void getHeaderUpcoming_returnsCorrectHeader(){
        League league = new League();
        league.setName("Bundesliga");

        String result =  leagueService.getHeaderUpcoming(league);

        assertEquals("Upcoming matches in Bundesliga", result);
    }

    @Test
    void getHeaderLatest_returnsCorrectHeader(){
        League league = new League();
        league.setName("Bundesliga");

        String result =  leagueService.getHeaderLatest(league);

        assertEquals("Latest matches in Bundesliga", result);
    }
}
