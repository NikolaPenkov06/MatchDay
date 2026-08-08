package bg.softuni.matchday.game;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.email.service.EmailService;
import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.repository.GameRepository;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.team.model.Level;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.Country;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddMatchRequest;
import bg.softuni.matchday.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceUTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private GameService gameService;

    @Test
    void getAllUpcomingLeagueGamesLimited_returnsNextSixGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2027, 1, 1,1,1,1));
        oldest.setLeague(league);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2027, 3, 1,1,1,1));
        older.setLeague(league);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2028, 1, 1,1,1,1));
        old.setLeague(league);

        Game middlest = new Game();
        middlest.setStartTime(LocalDateTime.of(2028, 4, 4,1,1,1));
        middlest.setLeague(league);

        Game middler = new Game();
        middler.setStartTime(LocalDateTime.of(2030, 1, 4,1,1,1));
        middler.setLeague(league);

        Game middle = new Game();
        middle.setStartTime(LocalDateTime.of(2030, 5, 4,1,1,1));
        middle.setLeague(league);

        Game late = new Game();
        late.setStartTime(LocalDateTime.of(2031, 1, 4,1,1,1));
        late.setLeague(league);

        Game later = new Game();
        later.setStartTime(LocalDateTime.of(2031, 5, 4,1,1,1));
        later.setLeague(league);

        Game latest = new Game();
        latest.setStartTime(LocalDateTime.of(2032, 1, 4,1,1,1));
        latest.setLeague(league);

        when(gameRepository.findAllByStartTimeAfter(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(List.of(
                        oldest, older, old, middlest,
                        middler, middle, late, later, latest
                )));

        List<Game> result = gameService.getUpcomingGamesLimited(league);

        verify(gameRepository).findAllByStartTimeAfter(any(LocalDateTime.class));

        assertEquals(6, result.size());

        assertEquals(oldest, result.get(0));
        assertEquals(older, result.get(1));
        assertEquals(old, result.get(2));
        assertEquals(middlest, result.get(3));
        assertEquals(middler, result.get(4));
        assertEquals(middle, result.get(5));

    }

    @Test
    void getAllUpcomingLeagueGames_returnsAllUpcomingGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2027, 1, 1,1,1,1));
        oldest.setLeague(league);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2027, 3, 1,1,1,1));
        older.setLeague(league);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2028, 1, 1,1,1,1));
        old.setLeague(league);

        when(gameRepository.findAllByStartTimeAfter(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(List.of(
                        older, oldest, old
                )));

        List<Game> result = gameService.getAllUpcomingGames(league);

        verify(gameRepository).findAllByStartTimeAfter(any(LocalDateTime.class));

        assertEquals(oldest, result.get(0));
        assertEquals(older, result.get(1));
        assertEquals(old, result.get(2));

    }

    @Test
    void getLatestLeagueGamesLimited_returnsLastSixGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2020, 1, 1,1,1,1));
        oldest.setLeague(league);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2020, 3, 1,1,1,1));
        older.setLeague(league);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2021, 1, 1,1,1,1));
        old.setLeague(league);

        Game middlest = new Game();
        middlest.setStartTime(LocalDateTime.of(2021, 4, 4,1,1,1));
        middlest.setLeague(league);

        Game middler = new Game();
        middler.setStartTime(LocalDateTime.of(2022, 1, 4,1,1,1));
        middler.setLeague(league);

        Game middle = new Game();
        middle.setStartTime(LocalDateTime.of(2022, 5, 4,1,1,1));
        middle.setLeague(league);

        Game late = new Game();
        late.setStartTime(LocalDateTime.of(2023, 1, 4,1,1,1));
        late.setLeague(league);

        Game later = new Game();
        later.setStartTime(LocalDateTime.of(2024, 5, 4,1,1,1));
        later.setLeague(league);

        Game latest = new Game();
        latest.setStartTime(LocalDateTime.of(2025, 1, 4,1,1,1));
        latest.setLeague(league);

        when(gameRepository.findAllByStartTimeBefore(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(List.of(
                        older, middlest, latest,
                        middler,oldest, middle, late, old, later
                )));

        List<Game> result = gameService.getLatestGamesLimited(league);

        verify(gameRepository).findAllByStartTimeBefore(any(LocalDateTime.class));

        assertEquals(6, result.size());

        assertEquals(latest, result.get(0));
        assertEquals(later, result.get(1));
        assertEquals(late, result.get(2));
        assertEquals(middle, result.get(3));
        assertEquals(middler, result.get(4));
        assertEquals(middlest, result.get(5));

    }

    @Test
    void getAllLatestLeagueGames_returnsAllLatestGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2020, 1, 1,1,1,1));
        oldest.setLeague(league);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2020, 3, 1,1,1,1));
        older.setLeague(league);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2021, 1, 1,1,1,1));
        old.setLeague(league);

        when(gameRepository.findAllByStartTimeBefore(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(List.of(
                        older, oldest, old
                )));

        List<Game> result = gameService.getAllLatestGames(league);

        verify(gameRepository).findAllByStartTimeBefore(any(LocalDateTime.class));

        assertEquals(old, result.get(0));
        assertEquals(older, result.get(1));
        assertEquals(oldest, result.get(2));

    }

    @Test
    void whenDrawIsProcessed_itIsSavedToRepository(){
        Game game = new Game();
        game.setHomePossessionPercentage(0);
        game.setAwayPossessionPercentage(0);

        Team home = new Team();
        home.setLevel(Level.BAD);

        Team away = new Team();
        away.setLevel(Level.BAD);

        game.setHomeTeam(home);
        game.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(game));

        when(userService.getAllUsers())
                .thenReturn(List.of());

        gameService.processGame();

        game.setHomeGoals(0);
        game.setAwayGoals(0);

        verify(gameRepository)
                .saveAll(anyList());
    }

    @Test
    void whenHomeWinIsProcessed_itIsSavedToRepository(){
        Game game = new Game();
        game.setHomePossessionPercentage(0);
        game.setAwayPossessionPercentage(0);

        Team home = new Team();
        home.setLevel(Level.BAD);

        Team away = new Team();
        away.setLevel(Level.BAD);

        game.setHomeTeam(home);
        game.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(game));

        when(userService.getAllUsers())
                .thenReturn(List.of());

        gameService.processGame();

        game.setHomeGoals(1);
        game.setAwayGoals(0);

        verify(gameRepository)
                .saveAll(anyList());
    }

    @Test
    void whenAwayWinIsProcessed_itIsSavedToRepository(){
        Game game = new Game();
        game.setHomePossessionPercentage(0);
        game.setAwayPossessionPercentage(0);

        Team home = new Team();
        home.setLevel(Level.BAD);

        Team away = new Team();
        away.setLevel(Level.BAD);

        game.setHomeTeam(home);
        game.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(game));

        when(userService.getAllUsers())
                .thenReturn(List.of());

        gameService.processGame();

        game.setHomeGoals(0);
        game.setAwayGoals(1);

        verify(gameRepository)
                .saveAll(anyList());
    }

    @Test
    void sendsEmailTo_HomeTeamFan(){
        Team home = new Team();
        home.setLevel(Level.BAD);

        Team away = new Team();
        away.setLevel(Level.GOOD);

        Game game = new Game();
        game.setHomeTeam(home);
        game.setAwayTeam(away);

        game.setHomePossessionPercentage(0);
        game.setAwayPossessionPercentage(0);

        User user = new User();
        user.setFavouriteTeam(home);
        user.setEmailsEnabled(true);
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        when(gameRepository.findAllByStartTimeBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(game));

        when(userService.getAllUsers())
                .thenReturn(List.of(user));

        gameService.processGame();

        verify(emailService).sendEmail(
                eq(user.getId()),
                eq(user.getEmail()),
                anyString(),
                anyString()
        );
    }

    private void runMatchup(Level homeLevel, Level awayLevel) {
        Team home = new Team();
        home.setLevel(homeLevel);

        Team away = new Team();
        away.setLevel(awayLevel);

        Game game = new Game();
        game.setHomeTeam(home);
        game.setAwayTeam(away);
        game.setHomePossessionPercentage(0);
        game.setAwayPossessionPercentage(0);
        game.setStartTime(LocalDateTime.now().minusDays(1));

        when(gameRepository.findAllByStartTimeBefore(any()))
                .thenReturn(List.of(game));

        when(userService.getAllUsers())
                .thenReturn(List.of());

        gameService.processGame();

        verify(teamService).save(home);
        verify(teamService).save(away);
    }

    @Test
    void badVsMedium_isProcessed() {
        runMatchup(Level.BAD, Level.MEDIUM);
    }

    @Test
    void badVsGood_isProcessed() {
        runMatchup(Level.BAD, Level.GOOD);
    }

    @Test
    void mediumVsBad_isProcessed() {
        runMatchup(Level.MEDIUM, Level.BAD);
    }

    @Test
    void mediumVsMedium_isProcessed() {
        runMatchup(Level.MEDIUM, Level.MEDIUM);
    }

    @Test
    void mediumVsGood_isProcessed() {
        runMatchup(Level.MEDIUM, Level.GOOD);
    }

    @Test
    void GoodVsGood_isProcessed() {
        runMatchup(Level.GOOD, Level.GOOD);
    }

    @Test
    void GoodVsMedium_isProcessed() {
        runMatchup(Level.GOOD, Level.MEDIUM);
    }

    @Test
    void GoodVsBad_isProcessed() {
        runMatchup(Level.GOOD, Level.BAD);
    }

    @Test
    void whenAddGame_thenGameIsSaved() {
        AddMatchRequest request = new AddMatchRequest();

        League league = new League();
        UUID leagueId = UUID.randomUUID();
        league.setName("Premier League");
        league.setId(leagueId);

        Team home = new Team();
        UUID homeId = UUID.randomUUID();
        home.setName("Arsenal");
        home.setLeague(league);
        home.setId(homeId);

        Team away = new Team();
        UUID awayId = UUID.randomUUID();
        away.setName("Norwich");
        away.setLeague(league);
        away.setId(awayId);

        request.setLeagueId(leagueId);
        request.setDate("12.12.2026");
        request.setTime("12:12");
        request.setAwayTeamId(awayId);
        request.setHomeTeamId(homeId);

        when(teamService.getById(homeId))
                .thenReturn(home);

        when(teamService.getById(awayId))
                .thenReturn(away);

        gameService.addGame(request);

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void getUpcomingTeamGamesLimited_returnsNextSixGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Team home = new Team();
        UUID homeId = UUID.randomUUID();
        home.setName("Arsenal");
        home.setLeague(league);
        home.setId(homeId);

        Team away = new Team();
        UUID awayId = UUID.randomUUID();
        away.setName("Norwich");
        away.setLeague(league);
        away.setId(awayId);

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2027, 1, 1,1,1,1));
        oldest.setLeague(league);
        oldest.setHomeTeam(home);
        oldest.setAwayTeam(away);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2027, 3, 1,1,1,1));
        older.setLeague(league);
        older.setHomeTeam(home);
        older.setAwayTeam(away);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2028, 1, 1,1,1,1));
        old.setLeague(league);
        old.setHomeTeam(home);
        old.setAwayTeam(away);

        Game middlest = new Game();
        middlest.setStartTime(LocalDateTime.of(2028, 4, 4,1,1,1));
        middlest.setLeague(league);
        middlest.setHomeTeam(home);
        middlest.setAwayTeam(away);

        Game middler = new Game();
        middler.setStartTime(LocalDateTime.of(2030, 1, 4,1,1,1));
        middler.setLeague(league);
        middler.setHomeTeam(home);
        middler.setAwayTeam(away);

        Game middle = new Game();
        middle.setStartTime(LocalDateTime.of(2030, 5, 4,1,1,1));
        middle.setLeague(league);
        middle.setHomeTeam(home);
        middle.setAwayTeam(away);

        Game late = new Game();
        late.setStartTime(LocalDateTime.of(2031, 1, 4,1,1,1));
        late.setLeague(league);
        late.setHomeTeam(home);
        late.setAwayTeam(away);

        Game later = new Game();
        later.setStartTime(LocalDateTime.of(2031, 5, 4,1,1,1));
        later.setLeague(league);
        later.setHomeTeam(home);
        later.setAwayTeam(away);

        Game latest = new Game();
        latest.setStartTime(LocalDateTime.of(2032, 1, 4,1,1,1));
        latest.setLeague(league);
        latest.setHomeTeam(home);
        latest.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeAfterAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>(List.of(
                older, middlest, latest,
                middler, oldest, middle,
                late, old, later
        )));

        when(gameRepository.findAllByStartTimeAfterAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>());

        List<Game> result = gameService.getUpcomingMatchesLimited(home);

        verify(gameRepository).findAllByStartTimeAfterAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        verify(gameRepository).findAllByStartTimeAfterAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        assertEquals(6, result.size());

        assertEquals(oldest, result.get(0));
        assertEquals(older, result.get(1));
        assertEquals(old, result.get(2));
        assertEquals(middlest, result.get(3));
        assertEquals(middler, result.get(4));
        assertEquals(middle, result.get(5));

    }

    @Test
    void getAllUpcomingTeamGames_returnsAllUpcomingGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Team home = new Team();
        UUID homeId = UUID.randomUUID();
        home.setName("Arsenal");
        home.setLeague(league);
        home.setId(homeId);

        Team away = new Team();
        UUID awayId = UUID.randomUUID();
        away.setName("Norwich");
        away.setLeague(league);
        away.setId(awayId);

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2027, 1, 1,1,1,1));
        oldest.setLeague(league);
        oldest.setHomeTeam(home);
        oldest.setAwayTeam(away);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2027, 3, 1,1,1,1));
        older.setLeague(league);
        older.setHomeTeam(home);
        older.setAwayTeam(away);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2028, 1, 1,1,1,1));
        old.setLeague(league);
        old.setHomeTeam(home);
        old.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeAfterAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>(List.of(
                older, oldest, old
        )));

        when(gameRepository.findAllByStartTimeAfterAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>());

        List<Game> result = gameService.getAllUpcomingMatches(home);

        verify(gameRepository).findAllByStartTimeAfterAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        verify(gameRepository).findAllByStartTimeAfterAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        assertEquals(oldest, result.get(0));
        assertEquals(older, result.get(1));
        assertEquals(old, result.get(2));

    }

    @Test
    void getLatestTeamGamesLimited_returnsLastSixGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Team home = new Team();
        UUID homeId = UUID.randomUUID();
        home.setName("Arsenal");
        home.setLeague(league);
        home.setId(homeId);

        Team away = new Team();
        UUID awayId = UUID.randomUUID();
        away.setName("Norwich");
        away.setLeague(league);
        away.setId(awayId);

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2020, 1, 1,1,1,1));
        oldest.setLeague(league);
        oldest.setHomeTeam(home);
        oldest.setAwayTeam(away);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2020, 3, 1,1,1,1));
        older.setLeague(league);
        older.setHomeTeam(home);
        older.setAwayTeam(away);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2021, 1, 1,1,1,1));
        old.setLeague(league);
        old.setHomeTeam(home);
        old.setAwayTeam(away);

        Game middlest = new Game();
        middlest.setStartTime(LocalDateTime.of(2021, 4, 4,1,1,1));
        middlest.setLeague(league);
        middlest.setHomeTeam(home);
        middlest.setAwayTeam(away);

        Game middler = new Game();
        middler.setStartTime(LocalDateTime.of(2022, 1, 4,1,1,1));
        middler.setLeague(league);
        middler.setHomeTeam(home);
        middler.setAwayTeam(away);

        Game middle = new Game();
        middle.setStartTime(LocalDateTime.of(2022, 5, 4,1,1,1));
        middle.setLeague(league);
        middle.setHomeTeam(home);
        middle.setAwayTeam(away);

        Game late = new Game();
        late.setStartTime(LocalDateTime.of(2023, 1, 4,1,1,1));
        late.setLeague(league);
        late.setHomeTeam(home);
        late.setAwayTeam(away);

        Game later = new Game();
        later.setStartTime(LocalDateTime.of(2023, 5, 4,1,1,1));
        later.setLeague(league);
        later.setHomeTeam(home);
        later.setAwayTeam(away);

        Game latest = new Game();
        latest.setStartTime(LocalDateTime.of(2024, 1, 4,1,1,1));
        latest.setLeague(league);
        latest.setHomeTeam(home);
        latest.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeBeforeAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>(List.of(
                older, middlest, latest,
                middler, oldest, middle,
                late, old, later
        )));

        when(gameRepository.findAllByStartTimeBeforeAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>());

        List<Game> result = gameService.getLatestMatchesLimited(home);

        verify(gameRepository).findAllByStartTimeBeforeAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        verify(gameRepository).findAllByStartTimeBeforeAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        assertEquals(6, result.size());

        assertEquals(latest, result.get(0));
        assertEquals(later, result.get(1));
        assertEquals(late, result.get(2));
        assertEquals(middle, result.get(3));
        assertEquals(middler, result.get(4));
        assertEquals(middlest, result.get(5));

    }

    @Test
    void getLatestTeamGames_returnsAllPastGamesSortedByDate(){
        League league = new League();
        league.setId(UUID.randomUUID());
        league.setName("Premier League");

        Team home = new Team();
        UUID homeId = UUID.randomUUID();
        home.setName("Arsenal");
        home.setLeague(league);
        home.setId(homeId);

        Team away = new Team();
        UUID awayId = UUID.randomUUID();
        away.setName("Norwich");
        away.setLeague(league);
        away.setId(awayId);

        Game oldest = new Game();
        oldest.setStartTime(LocalDateTime.of(2020, 1, 1,1,1,1));
        oldest.setLeague(league);
        oldest.setHomeTeam(home);
        oldest.setAwayTeam(away);

        Game older = new Game();
        older.setStartTime(LocalDateTime.of(2020, 3, 1,1,1,1));
        older.setLeague(league);
        older.setHomeTeam(home);
        older.setAwayTeam(away);

        Game old = new Game();
        old.setStartTime(LocalDateTime.of(2021, 1, 1,1,1,1));
        old.setLeague(league);
        old.setHomeTeam(home);
        old.setAwayTeam(away);

        when(gameRepository.findAllByStartTimeBeforeAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>(List.of(
                older, oldest, old
        )));

        when(gameRepository.findAllByStartTimeBeforeAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        )).thenReturn(new ArrayList<>());

        List<Game> result = gameService.getAllLatestMatches(home);

        verify(gameRepository).findAllByStartTimeBeforeAndHomeTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        verify(gameRepository).findAllByStartTimeBeforeAndAwayTeamName(
                any(LocalDateTime.class),
                eq("Arsenal")
        );

        assertEquals(old, result.get(0));
        assertEquals(older, result.get(1));
        assertEquals(oldest, result.get(2));

    }

    @Test
    void getById_returnsRightGame(){
        UUID uuid = UUID.randomUUID();

        Game game = new Game();
        game.setId(uuid);

        when(gameRepository.findById(uuid))
                .thenReturn(Optional.of(game));

        Game result = gameService.getById(uuid);

        assertEquals(game, result);

    }

    @Test
    void ifInvalidDate_returnFalse(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("59.29.1000");

        boolean result = gameService.checkIfDateIsValid(addMatchRequest);

        assertFalse(result);

    }

    @Test
    void ifDayHigherThanMonthDays_returnFalse(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("31.04.2025");

        boolean result = gameService.checkIfDateIsValid(addMatchRequest);

        assertFalse(result);

    }

    @Test
    void ifDayHigherThanFebruaryDays_returnFalse(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("30.02.2025");

        boolean result = gameService.checkIfDateIsValid(addMatchRequest);

        assertFalse(result);

    }

    @Test
    void ifDateIsCorrect_returnTrue(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("30.01.2025");

        boolean result = gameService.checkIfDateIsValid(addMatchRequest);

        assertTrue(result);

    }

    @Test
    void ifTimeIsCorrect_returnTrue(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setTime("21:00");

        boolean result = gameService.checkIfTimeIsValid(addMatchRequest);

        assertTrue(result);

    }

    @Test
    void ifDateIsInSeason_returnTrue(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("30.01.2027");

        boolean result = gameService.checkIfDateIsInSeason(addMatchRequest);

        assertTrue(result);

    }

    @Test
    void ifYearIsOutsideOfSeason_returnFalse(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("30.01.2029");

        boolean result = gameService.checkIfDateIsInSeason(addMatchRequest);

        assertFalse(result);

    }

    @Test
    void ifDateIsBeforeSeason_returnFalse(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("30.06.2026");

        boolean result = gameService.checkIfDateIsInSeason(addMatchRequest);

        assertFalse(result);

    }

    @Test
    void ifDateIsAfterSeason_returnFalse(){
        AddMatchRequest addMatchRequest = new AddMatchRequest();
        addMatchRequest.setDate("30.07.2027");

        boolean result = gameService.checkIfDateIsInSeason(addMatchRequest);

        assertFalse(result);

    }

    @Test
    void getSubject_homeFavouriteTeamWins_returnsWinMessage() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team chelsea = new Team();
        chelsea.setName("Chelsea");

        User user = new User();
        user.setFavouriteTeam(arsenal);

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(chelsea);
        game.setHomeGoals(3);
        game.setAwayGoals(1);

        String result = gameService.getSubject(game, user);

        assertEquals(
                "Arsenal win 3-1 against Chelsea",
                result
        );
    }

    @Test
    void getSubject_favouriteTeamDraws_returnsDrawMessage() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team chelsea = new Team();
        chelsea.setName("Chelsea");

        User user = new User();
        user.setFavouriteTeam(arsenal);

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(chelsea);
        game.setHomeGoals(0);
        game.setAwayGoals(0);

        String result = gameService.getSubject(game, user);

        assertEquals(
                "Arsenal draw 0-0 against Chelsea",
                result
        );
    }

    @Test
    void getSubject_homeFavouriteTeamLoses_returnsLossMessage() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team chelsea = new Team();
        chelsea.setName("Chelsea");

        User user = new User();
        user.setFavouriteTeam(arsenal);

        Game game = new Game();
        game.setHomeTeam(arsenal);
        game.setAwayTeam(chelsea);
        game.setHomeGoals(0);
        game.setAwayGoals(1);

        String result = gameService.getSubject(game, user);

        assertEquals(
                "Arsenal lose 0-1 against Chelsea",
                result
        );
    }

    @Test
    void getSubject_awayFavouriteTeamWins_returnsWinMessage() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team chelsea = new Team();
        chelsea.setName("Chelsea");

        User user = new User();
        user.setFavouriteTeam(arsenal);

        Game game = new Game();
        game.setHomeTeam(chelsea);
        game.setAwayTeam(arsenal);
        game.setHomeGoals(0);
        game.setAwayGoals(2);

        String result = gameService.getSubject(game, user);

        assertEquals(
                "Arsenal win 0-2 against Chelsea",
                result
        );
    }

    @Test
    void getSubject_homeFavouriteTeamLoses_returnsDrawMessage() {
        Team arsenal = new Team();
        arsenal.setName("Arsenal");

        Team chelsea = new Team();
        chelsea.setName("Chelsea");

        User user = new User();
        user.setFavouriteTeam(arsenal);

        Game game = new Game();
        game.setHomeTeam(chelsea);
        game.setAwayTeam(arsenal);
        game.setHomeGoals(2);
        game.setAwayGoals(0);

        String result = gameService.getSubject(game, user);

        assertEquals(
                "Arsenal lose 2-0 against Chelsea",
                result
        );
    }
}
