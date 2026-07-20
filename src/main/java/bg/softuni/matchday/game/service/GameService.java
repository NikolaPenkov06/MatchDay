package bg.softuni.matchday.game.service;

import bg.softuni.matchday.email.service.EmailService;
import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.repository.GameRepository;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.team.model.Level;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.repository.TeamRepository;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.Role;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.AddMatchRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final TeamService teamService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public GameService(GameRepository gameRepository, TeamService teamService, UserService userService, EmailService emailService) {
        this.gameRepository = gameRepository;
        this.teamService = teamService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public List<Game> getUpcomingGamesLimited(League league) {
        List<Game> games = new ArrayList<>(
                gameRepository.findAllByStartTimeAfter(LocalDateTime.now())
                        .stream()
                        .filter(game -> game.getLeague().equals(league))
                        .toList()
        );
        games.sort(Comparator.comparing(Game::getStartTime));

        if (games.size() > 6) {
            games = games.subList(0, 6);
        }

        return games;
    }

    public List<Game> getAllUpcomingGames(League league) {
        List<Game> games = new ArrayList<>(
                gameRepository.findAllByStartTimeAfter(LocalDateTime.now())
                        .stream()
                        .filter(game -> game.getLeague().equals(league))
                        .toList()
        );
        games.sort(Comparator.comparing(Game::getStartTime));

        return games;
    }

    public List<Game> getLatestGamesLimited(League league) {
        List<Game> games = new ArrayList<>(
                gameRepository.findAllByStartTimeBefore(LocalDateTime.now())
                        .stream()
                        .filter(game -> game.getLeague().equals(league))
                        .toList()
        );
        games.sort(Comparator.comparing(Game::getStartTime).reversed());

        if (games.size() > 6) {
            games = games.subList(0, 6);
        }

        return games;
    }

    public List<Game> getAllLatestGames(League league) {
        List<Game> games = new ArrayList<>(
                gameRepository.findAllByStartTimeBefore(LocalDateTime.now())
                        .stream()
                        .filter(game -> game.getLeague().equals(league))
                        .toList()
        );
        games.sort(Comparator.comparing(Game::getStartTime).reversed());

        return games;
    }

    public void processGame() {
        List<Game> gamesToProcess = gameRepository.findAllByStartTimeBefore(LocalDateTime.now())
                .stream()
                .filter(g -> g.getHomePossessionPercentage() == 0 && g.getAwayPossessionPercentage() == 0)
                .toList();

        List<User> users = userService.getAllUsers();

        for (Game game : gamesToProcess) {

            Level homeLevel = game.getHomeTeam().getLevel();
            Level awayLevel = game.getAwayTeam().getLevel();

            switch (homeLevel) {
                case BAD:
                    switch (awayLevel) {
                        case BAD:
                            sameLevelGame(game);
                            break;
                        case MEDIUM:
                            badAgainstMedium(game);
                            break;
                        case GOOD:
                            badAgainstGood(game);
                            break;
                    }
                    break;
                case MEDIUM:
                    switch (awayLevel) {
                        case BAD:
                            badAgainstMedium(game);
                            swapHomeAway(game);
                            break;
                        case MEDIUM:
                            sameLevelGame(game);
                            break;
                        case GOOD:
                            mediumAgainstGood(game);
                            break;
                    }
                    break;
                case GOOD:
                    switch (awayLevel) {
                        case BAD:
                            badAgainstGood(game);
                            swapHomeAway(game);
                            break;
                        case MEDIUM:
                            mediumAgainstGood(game);
                            swapHomeAway(game);
                            break;
                        case GOOD:
                            derby(game);
                            break;
                    }
                    break;
            }

            if (game.getHomeGoals() > game.getAwayGoals()) {

                game.getHomeTeam().setWins(game.getHomeTeam().getWins() + 1);
                game.getAwayTeam().setLosses(game.getAwayTeam().getLosses() + 1);
                game.getHomeTeam().setPoints(game.getHomeTeam().getPoints() + 3);

            } else if (game.getAwayGoals() > game.getHomeGoals()) {

                game.getAwayTeam().setWins(game.getAwayTeam().getWins() + 1);
                game.getHomeTeam().setLosses(game.getHomeTeam().getLosses() + 1);
                game.getAwayTeam().setPoints(game.getAwayTeam().getPoints() + 3);

            } else {

                game.getAwayTeam().setDraws(game.getAwayTeam().getDraws() + 1);
                game.getHomeTeam().setDraws(game.getHomeTeam().getDraws() + 1);
                game.getAwayTeam().setPoints(game.getAwayTeam().getPoints() + 1);
                game.getHomeTeam().setPoints(game.getHomeTeam().getPoints() + 1);

            }

            game.getHomeTeam().setMatchesPlayed(game.getHomeTeam().getMatchesPlayed() + 1);
            game.getAwayTeam().setMatchesPlayed(game.getAwayTeam().getMatchesPlayed() + 1);

            game.getHomeTeam().setGoalsAgainst(game.getHomeTeam().getGoalsAgainst() + game.getAwayGoals());
            game.getHomeTeam().setGoalsFor(game.getHomeTeam().getGoalsFor() + game.getHomeGoals());

            game.getAwayTeam().setGoalsAgainst(game.getAwayTeam().getGoalsAgainst() + game.getHomeGoals());
            game.getAwayTeam().setGoalsFor(game.getAwayTeam().getGoalsFor() + game.getAwayGoals());

            teamService.save(game.getHomeTeam());
            teamService.save(game.getAwayTeam());

            for (User user : users){
                if((game.getHomeTeam().equals(user.getFavouriteTeam()) || game.getAwayTeam().equals(user.getFavouriteTeam())) && user.isEmailsEnabled()){
                    emailService.sendEmail(user.getId(), user.getEmail(), getSubject(game, user), getBody(game, user));
                }
            }
        }

        gameRepository.saveAll(gamesToProcess);


    }

    private static void badAgainstGood(Game game) {
        int homeGoals = ThreadLocalRandom.current().nextInt(0, 3);
        int awayGoals = ThreadLocalRandom.current().nextInt(0, 8);

        game.setHomeGoals(homeGoals);
        game.setAwayGoals(awayGoals);
        game.setHomeCorners(ThreadLocalRandom.current().nextInt(0, 5));
        game.setAwayCorners(ThreadLocalRandom.current().nextInt(3, 11));
        game.setHomeFouls(ThreadLocalRandom.current().nextInt(5, 23));
        game.setAwayFouls(ThreadLocalRandom.current().nextInt(3, 12));
        game.setHomeOffsides(ThreadLocalRandom.current().nextInt(0, 5));
        game.setAwayOffsides(ThreadLocalRandom.current().nextInt(2, 11));
        game.setHomePossessionPercentage(ThreadLocalRandom.current().nextInt(15, 41));
        game.setAwayPossessionPercentage(100 - game.getHomePossessionPercentage());
        game.setHomeShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 5));
        game.setAwayShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 12));
        game.setHomeShotsOffTarget(ThreadLocalRandom.current().nextInt(1, 6));
        game.setAwayShotsOffTarget(ThreadLocalRandom.current().nextInt(2, 12));
    }

    private static void sameLevelGame(Game game) {
        int homeGoals = ThreadLocalRandom.current().nextInt(0, 4);
        int awayGoals = ThreadLocalRandom.current().nextInt(0, 4);

        game.setHomeGoals(homeGoals);
        game.setAwayGoals(awayGoals);
        game.setHomeCorners(ThreadLocalRandom.current().nextInt(1, 8));
        game.setAwayCorners(ThreadLocalRandom.current().nextInt(1, 8));
        game.setHomeFouls(ThreadLocalRandom.current().nextInt(5, 21));
        game.setAwayFouls(ThreadLocalRandom.current().nextInt(5, 21));
        game.setHomeOffsides(ThreadLocalRandom.current().nextInt(0, 8));
        game.setAwayOffsides(ThreadLocalRandom.current().nextInt(0, 8));
        game.setHomePossessionPercentage(ThreadLocalRandom.current().nextInt(25, 76));
        game.setAwayPossessionPercentage(100 - game.getHomePossessionPercentage());
        game.setHomeShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 9));
        game.setAwayShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 9));
        game.setHomeShotsOffTarget(ThreadLocalRandom.current().nextInt(2, 10));
        game.setAwayShotsOffTarget(ThreadLocalRandom.current().nextInt(2, 10));
    }

    private static void badAgainstMedium(Game game) {
        int homeGoals = ThreadLocalRandom.current().nextInt(0, 3);
        int awayGoals = ThreadLocalRandom.current().nextInt(0, 5);

        game.setHomeGoals(homeGoals);
        game.setAwayGoals(awayGoals);
        game.setHomeCorners(ThreadLocalRandom.current().nextInt(1, 7));
        game.setAwayCorners(ThreadLocalRandom.current().nextInt(1, 9));
        game.setHomeFouls(ThreadLocalRandom.current().nextInt(6, 23));
        game.setAwayFouls(ThreadLocalRandom.current().nextInt(4, 20));
        game.setHomeOffsides(ThreadLocalRandom.current().nextInt(0, 5));
        game.setAwayOffsides(ThreadLocalRandom.current().nextInt(2, 11));
        game.setHomePossessionPercentage(ThreadLocalRandom.current().nextInt(25, 56));
        game.setAwayPossessionPercentage(100 - game.getHomePossessionPercentage());
        game.setHomeShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 7));
        game.setAwayShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 10));
        game.setHomeShotsOffTarget(ThreadLocalRandom.current().nextInt(1, 8));
        game.setAwayShotsOffTarget(ThreadLocalRandom.current().nextInt(3, 10));
    }

    private static void mediumAgainstGood(Game game) {
        int homeGoals = ThreadLocalRandom.current().nextInt(0, 4);
        int awayGoals = ThreadLocalRandom.current().nextInt(0, 7);

        game.setHomeGoals(homeGoals);
        game.setAwayGoals(awayGoals);
        game.setHomeCorners(ThreadLocalRandom.current().nextInt(1, 8));
        game.setAwayCorners(ThreadLocalRandom.current().nextInt(1, 11));
        game.setHomeFouls(ThreadLocalRandom.current().nextInt(4, 19));
        game.setAwayFouls(ThreadLocalRandom.current().nextInt(4, 15));
        game.setHomeOffsides(ThreadLocalRandom.current().nextInt(0, 5));
        game.setAwayOffsides(ThreadLocalRandom.current().nextInt(2, 11));
        game.setHomePossessionPercentage(ThreadLocalRandom.current().nextInt(20, 64));
        game.setAwayPossessionPercentage(100 - game.getHomePossessionPercentage());
        game.setHomeShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 7));
        game.setAwayShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 11));
        game.setHomeShotsOffTarget(ThreadLocalRandom.current().nextInt(1, 8));
        game.setAwayShotsOffTarget(ThreadLocalRandom.current().nextInt(3, 11));
    }

    private static void derby(Game game) {
        int homeGoals = ThreadLocalRandom.current().nextInt(0, 5);
        int awayGoals = ThreadLocalRandom.current().nextInt(0, 5);

        game.setHomeGoals(homeGoals);
        game.setAwayGoals(awayGoals);
        game.setHomeCorners(ThreadLocalRandom.current().nextInt(1, 8));
        game.setAwayCorners(ThreadLocalRandom.current().nextInt(1, 8));
        game.setHomeFouls(ThreadLocalRandom.current().nextInt(6, 16));
        game.setAwayFouls(ThreadLocalRandom.current().nextInt(6, 16));
        game.setHomeOffsides(ThreadLocalRandom.current().nextInt(4, 13));
        game.setAwayOffsides(ThreadLocalRandom.current().nextInt(4, 13));
        game.setHomePossessionPercentage(ThreadLocalRandom.current().nextInt(35, 66));
        game.setAwayPossessionPercentage(100 - game.getHomePossessionPercentage());
        game.setHomeShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 8));
        game.setAwayShotsOnTarget(ThreadLocalRandom.current().nextInt(game.getHomeGoals(), game.getHomeGoals() + 8));
        game.setHomeShotsOffTarget(ThreadLocalRandom.current().nextInt(2, 8));
        game.setAwayShotsOffTarget(ThreadLocalRandom.current().nextInt(2, 8));
    }

    private static void swapHomeAway(Game game) {
        int tempGoals = game.getHomeGoals();
        game.setHomeGoals(game.getAwayGoals());
        game.setAwayGoals(tempGoals);

        int tempCorners = game.getHomeCorners();
        game.setHomeCorners(game.getAwayCorners());
        game.setAwayCorners(tempCorners);

        int tempFouls = game.getHomeFouls();
        game.setHomeFouls(game.getAwayFouls());
        game.setAwayFouls(tempFouls);

        int tempPossession = game.getHomePossessionPercentage();
        game.setHomePossessionPercentage(game.getAwayPossessionPercentage());
        game.setAwayPossessionPercentage(tempPossession);

        int tempOnTarget = game.getHomeShotsOnTarget();
        game.setHomeShotsOnTarget(game.getAwayShotsOnTarget());
        game.setAwayShotsOnTarget(tempOnTarget);

        int tempOffTarget = game.getHomeShotsOffTarget();
        game.setHomeShotsOffTarget(game.getAwayShotsOffTarget());
        game.setAwayShotsOffTarget(tempOffTarget);
    }

    public void addGame(AddMatchRequest addMatchRequest) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(addMatchRequest.getDate(), dateFormatter);
        LocalTime time = LocalTime.parse(addMatchRequest.getTime(), timeFormatter);

        LocalDateTime startTime = LocalDateTime.of(date, time);

        Game game = Game.builder()
                .homeTeam(teamService.getById(addMatchRequest.getHomeTeamId()))
                .awayTeam(teamService.getById(addMatchRequest.getAwayTeamId()))
                .league(teamService.getById(addMatchRequest.getHomeTeamId()).getLeague())
                .startTime(startTime)
                .homeGoals(0)
                .homeCorners(0)
                .homeFouls(0)
                .homeOffsides(0)
                .homePossessionPercentage(0)
                .homeShotsOnTarget(0)
                .homeShotsOffTarget(0)
                .awayGoals(0)
                .awayCorners(0)
                .awayFouls(0)
                .awayOffsides(0)
                .awayPossessionPercentage(0)
                .awayShotsOnTarget(0)
                .awayShotsOffTarget(0)
                .build();

        gameRepository.save(game);

    }

    public List<Game> getUpcomingMatchesLimited(Team team) {
        List<Game> games = new ArrayList<>(getAllUpcomingGamesTeam(team));

        games.sort(Comparator.comparing(Game::getStartTime));

        if (games.size() > 6) {
            games = games.subList(0, 6);
        }

        return games;
    }

    public List<Game> getAllUpcomingMatches(Team team) {
        List<Game> games = new ArrayList<>(getAllUpcomingGamesTeam(team));

        games.sort(Comparator.comparing(Game::getStartTime));

        return games;
    }


    public List<Game> getLatestMatchesLimited(Team team) {
        List<Game> games = new ArrayList<>(getAllLatestGamesTeam(team));

        games.sort(Comparator.comparing(Game::getStartTime).reversed());

        if (games.size() > 6) {
            games = games.subList(0, 6);
        }

        return games;
    }

    public List<Game> getAllLatestMatches(Team team) {
        List<Game> games = new ArrayList<>(getAllLatestGamesTeam(team));

        games.sort(Comparator.comparing(Game::getStartTime));

        return games;
    }


    public Game getById(UUID id) {
        return gameRepository.findById(id).orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
    }

    public boolean checkIfDateIsValid(AddMatchRequest addMatchRequest) {
        int day = Integer.parseInt(addMatchRequest.getDate().substring(0, 2));
        int month = Integer.parseInt(addMatchRequest.getDate().substring(3, 5));
        if (month < 1 || month > 12 || day < 1 || day > 31) {
            return false;
        }

        switch (month) {
            case 4, 6, 9, 11:
                if (day > 30) {
                    return false;
                }
                break;
            case 2:
                if (day > 28) {
                    return false;
                }
                break;

        }

        return true;

    }

    public boolean checkIfTimeIsValid(AddMatchRequest addMatchRequest) {
        int hours = Integer.parseInt(addMatchRequest.getTime().substring(0, 2));
        int minutes = Integer.parseInt(addMatchRequest.getTime().substring(3, 5));
        return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;

    }

    public boolean checkIfDateIsInSeason(AddMatchRequest addMatchRequest) {
        int month = Integer.parseInt(addMatchRequest.getDate().substring(3, 5));
        int year = Integer.parseInt(addMatchRequest.getDate().substring(6, 10));

        if (year < 2026 || year > 2027) {
            return false;
        }

        if (year == 2026 && month < 7) {
            return false;
        } else if (year == 2027 && month > 5) {
            return false;
        }

        return true;

    }

    public List<Game> getAllUpcomingGamesTeam(Team team) {
        List<Game> upcomingGames = new ArrayList<>();
        upcomingGames.addAll(gameRepository.findAllByStartTimeAfterAndHomeTeamName(LocalDateTime.now(), team.getName()));
        upcomingGames.addAll(gameRepository.findAllByStartTimeAfterAndAwayTeamName(LocalDateTime.now(), team.getName()));
        return upcomingGames;
    }

    public List<Game> getAllLatestGamesTeam(Team team) {
        List<Game> latestGames = new ArrayList<>();
        latestGames.addAll(gameRepository.findAllByStartTimeBeforeAndHomeTeamName(LocalDateTime.now(), team.getName()));
        latestGames.addAll(gameRepository.findAllByStartTimeBeforeAndAwayTeamName(LocalDateTime.now(), team.getName()));
        return latestGames;

    }

    public String getSubject(Game game, User user) {
        int homeGoals = game.getHomeGoals();
        int awayGoals = game.getAwayGoals();
        String outcome = "";

        if (homeGoals == awayGoals) {
            outcome = "draw";
        }

        if (game.getHomeTeam().equals(user.getFavouriteTeam())) {
            if (homeGoals > awayGoals) {
                outcome = "win";
            } else if (awayGoals > homeGoals) {
                outcome = "lose";
            }
            return "%s %s %d-%d against %s".formatted(game.getHomeTeam().getName(), outcome, homeGoals, awayGoals, game.getAwayTeam().getName());
        } else {
            if (awayGoals > homeGoals) {
                outcome = "win";
            } else if (homeGoals > awayGoals) {
                outcome = "lose";
            }
            return "%s %s %d-%d against %s".formatted(game.getAwayTeam().getName(), outcome, homeGoals, awayGoals, game.getHomeTeam().getName());
        }

    }

    public String getBody(Game game, User user) {
        String message = "";
        int homeGoals = game.getHomeGoals();
        int awayGoals = game.getAwayGoals();
        String result = homeGoals + "-" + awayGoals;
        String homeTeam = game.getHomeTeam().getName();
        String awayTeam = game.getAwayTeam().getName();
        String stadium = game.getHomeTeam().getStadiumName();
        String manager = user.getFavouriteTeam().getCoachName();

        if (game.getHomeTeam().equals(user.getFavouriteTeam())) {
            if (homeGoals > awayGoals) {
                switch (result) {
                    case "1-0":
                        message = "%s get a narrow 1-0 win against %s at home. Both teams defended brilliantly throughout the match, making clear chances hard to come by. In the end, %s found the decisive goal and held on to secure all three points.".formatted(homeTeam, awayTeam, homeTeam);
                        break;
                    case "2-0":
                        message = "%s earn a convincing 2-0 home victory over %s. They looked in control for most of the match and took their chances well when the opportunities came. Backed by the home crowd at %s, they secured a clean sheet and another valuable three points.".formatted(homeTeam, awayTeam, stadium);
                        break;
                    case "3-0":
                        message = "%s put on an outstanding display to defeat %s 3-0 at home. Clinical finishing and a disciplined defensive performance left the visitors with no way back into the match. It was another excellent result for %s under the guidance of %s.".formatted(homeTeam, awayTeam, homeTeam, manager);
                        break;
                    case "4-0", "5-0", "6-0", "7-0":
                        message = "%s produce a sensational performance to defeat %s %s at home. From the opening whistle, the hosts were in complete control, dominating both ends of the pitch and giving their opponents little opportunity to respond. It was another outstanding display under the guidance of %s, whose side fully deserved the emphatic victory.".formatted(homeTeam, awayTeam, result, manager);
                        break;
                    case "2-1":
                        message = "%s secure a hard-fought 2-1 victory over %s at %s. Despite a determined effort from the visitors, the home side held their nerve and saw out the match to collect all three points.".formatted(homeTeam, awayTeam, stadium);
                        break;
                    case "3-1":
                        message = "%s celebrate a convincing 3-1 home win against %s. The hosts controlled the game for long periods, combining sharp attacking play with confident defending. It was another strong performance from %s under the guidance of %s.".formatted(homeTeam, awayTeam, homeTeam, manager);
                        break;
                    case "4-1", "5-1":
                        message = "%s thrilled the fans at %s with an emphatic %s victory over %s. Their attacking football proved too much for the visitors, who struggled to contain wave after wave of pressure. It was another memorable home performance and a fully deserved three points.".formatted(homeTeam, stadium, result, awayTeam);
                        break;
                    case "3-2":
                        message = "%s come out on top in an entertaining 3-2 victory over %s at home. Both sides created plenty of chances in an action-packed encounter, but the hosts showed their quality when it mattered most. Despite late pressure from the visitors, %s held on to secure a memorable three points.".formatted(homeTeam, awayTeam, homeTeam);
                        break;
                    case "4-2":
                        message = "%s delight the supporters at %s with an impressive 4-2 victory over %s. It was an open and entertaining contest, but the home side consistently looked the more dangerous team in attack. Their clinical finishing proved to be the difference as they wrapped up another deserved win.".formatted(homeTeam, stadium, awayTeam);
                        break;
                    default:
                        int difference = homeGoals - awayGoals;
                        switch (difference) {
                            case 1:
                                message = "%s come out on top in a thrilling %s home victory over %s. It was an end-to-end contest filled with attacking football, with both sides creating plenty of chances throughout the match. In the end, %s held their nerve to edge out the visitors and secure a dramatic three points.".formatted(homeTeam, result, awayTeam, homeTeam);
                                break;
                            case 2:
                                message = "%s secure an entertaining %s home victory over %s. Both teams showed plenty of attacking intent and found the back of the net several times, but the hosts consistently had the edge going forward. It was another exciting performance that earned %s a well-deserved three points.".formatted(homeTeam, result, awayTeam, homeTeam);
                                break;
                            case 3:
                                message = "%s produce an excellent display to beat %s %s at home. The hosts controlled the game from the early stages and combined attacking quality with disciplined defending to stay comfortably ahead throughout the match. It was another deserved victory that showcased the team's strength from start to finish.".formatted(homeTeam, awayTeam, result);
                                break;
                            case 4, 5, 6:
                                message = "%s put on an unforgettable display to beat %s %s at home. Their attacking quality was simply too much for the visitors, who struggled to cope throughout the match despite finding the back of the net. It was another outstanding performance under the guidance of %s, whose side looked unstoppable from the opening whistle.".formatted(homeTeam, awayTeam, result, manager);
                                break;
                        }
                }
            } else if (awayGoals > homeGoals) {
                switch (result) {
                    case "0-1":
                        message = "%s suffer a narrow 1-0 home defeat to %s. Both teams defended well throughout the match, with clear chances few and far between. In the end, %s found the only goal of the game, leaving the hosts empty-handed.".formatted(homeTeam, awayTeam, awayTeam);
                        break;

                    case "0-2":
                        message = "%s fall to a 2-0 home defeat against %s. Despite the support at %s, the hosts struggled to create enough clear opportunities, while the visitors took their chances and secured a deserved victory.".formatted(homeTeam, awayTeam, stadium);
                        break;

                    case "0-3":
                        message = "%s are beaten 3-0 at home by %s after a difficult afternoon. The visitors proved clinical in front of goal and rarely looked troubled defensively. %s will be looking for a response from his side in the next match.".formatted(homeTeam, awayTeam, manager);
                        break;

                    case "0-4", "0-5", "0-6", "0-7":
                        message = "%s endure a heavy %s home defeat to %s. The visitors dominated from the opening whistle, leaving the hosts with little opportunity to get back into the contest. It was a disappointing performance for %s and his team.".formatted(homeTeam, result, awayTeam, manager);
                        break;

                    case "1-2":
                        message = "%s suffer a frustrating 2-1 home defeat to %s at %s. The hosts battled throughout the match and found the back of the net, but the visitors ultimately did enough to leave with all three points.".formatted(homeTeam, awayTeam, stadium);
                        break;

                    case "1-3":
                        message = "%s are defeated 3-1 at home by %s. Although the hosts managed to score, they struggled to contain the visitors for long periods of the match. %s will be hoping his side can quickly put this result behind them.".formatted(homeTeam, awayTeam, manager);
                        break;

                    case "1-4", "1-5":
                        message = "%s disappoint the home crowd at %s with a %s defeat against %s. The visitors looked dangerous every time they attacked and punished the hosts throughout the match. It was a difficult afternoon from start to finish.".formatted(homeTeam, stadium, result, awayTeam);
                        break;

                    case "2-3":
                        message = "%s come up just short in an entertaining 3-2 home defeat to %s. It was an open contest with chances at both ends, but the visitors proved slightly more clinical when it mattered most. Despite a spirited effort, the hosts were left without any points.".formatted(homeTeam, awayTeam);
                        break;

                    case "2-4":
                        message = "%s are beaten 4-2 by %s at %s in an entertaining encounter. The hosts showed flashes of quality going forward, but defensive mistakes proved costly as the visitors secured a deserved victory.".formatted(homeTeam, awayTeam, stadium);
                        break;
                    default:
                        int difference = awayGoals - homeGoals;
                        switch (difference) {
                            case 1:
                                message = "%s fall to a narrow %s home defeat against %s. It was an end-to-end contest with both sides creating plenty of opportunities, but the visitors made the decisive moments count. Despite a determined effort, the hosts were unable to rescue a result.".formatted(homeTeam, result, awayTeam);
                                break;
                            case 2:
                                message = "%s suffer a disappointing %s home defeat to %s. Both teams looked dangerous going forward and found the back of the net on several occasions, but the visitors consistently had the edge in attack. The hosts were left with plenty to reflect on after the final whistle.".formatted(homeTeam, result, awayTeam);
                                break;
                            case 3:
                                message = "%s are comfortably beaten %s at home by %s. The visitors controlled the match from the early stages, combining clinical finishing with a disciplined defensive display. It was a deserved victory for %s from start to finish.".formatted(homeTeam, result, awayTeam, awayTeam);
                                break;
                            case 4, 5, 6:
                                message = "%s endure a heavy %s home defeat against %s. The visitors proved far too strong throughout the match, dominating the game with relentless attacking football while giving the hosts little chance to recover. %s will be hoping his side can quickly bounce back from a difficult afternoon.".formatted(homeTeam, result, awayTeam, manager);
                                break;
                        }


                }
            } else {
                switch (result) {
                    case "0-0":
                        message = "%s and %s play out a hard-fought goalless draw at %s. Both sides remained well organized defensively throughout the match, making clear-cut chances difficult to create. In the end, neither team could find the breakthrough as they settled for a point each.".formatted(homeTeam, awayTeam, stadium);
                        break;

                    case "1-1":
                        message = "%s and %s share the spoils in an entertaining 1-1 draw. Both teams enjoyed spells of pressure and managed to find the back of the net, but neither could produce the decisive goal. %s will take positives from the performance while knowing there was still room for improvement.".formatted(homeTeam, awayTeam, manager);
                        break;

                    case "2-2":
                        message = "%s and %s deliver an exciting 2-2 draw at %s. The match was played at a high tempo, with both sides showing attacking intent and responding well whenever they fell behind. The result was a fair reflection of an evenly contested encounter.".formatted(homeTeam, awayTeam, stadium);
                        break;

                    case "3-3", "4-4", "5-5", "6-6", "7-7":
                        message = "%s and %s produce a spectacular %s draw in one of the most entertaining matches of the season. The fans at %s witnessed relentless attacking football, countless chances and dramatic momentum swings throughout the contest. Neither side could find the decisive goal, but both teams earned applause after an unforgettable encounter.".formatted(homeTeam, awayTeam, result, stadium);
                        break;
                }
            }

        } else {
            if (awayGoals > homeGoals) {
                switch (result) {
                    case "0-1":
                        message = "%s get a narrow 1-0 away win against %s. Both teams defended brilliantly throughout the match, making clear chances hard to come by. In the end, %s found the decisive goal and left %s with all three points.".formatted(awayTeam, homeTeam, awayTeam, stadium);
                        break;

                    case "0-2":
                        message = "%s earn a convincing 2-0 away victory over %s. They looked in control for most of the match and took their chances well when the opportunities came. Leaving %s with all three points, they secured a clean sheet and another excellent result on the road.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    case "0-3":
                        message = "%s put on an outstanding display to defeat %s 3-0 away from home. Clinical finishing and a disciplined defensive performance left the hosts with no way back into the match. It was another excellent result for %s under the guidance of %s.".formatted(awayTeam, homeTeam, awayTeam, manager);
                        break;

                    case "0-4", "0-5", "0-6", "0-7":
                        message = "%s produce a sensational performance to defeat %s %s away from home. From the opening whistle, the visitors were in complete control, dominating both ends of the pitch and giving the hosts little opportunity to respond. It was another outstanding display under the guidance of %s, whose side fully deserved the emphatic victory.".formatted(awayTeam, homeTeam, result, manager);
                        break;

                    case "1-2":
                        message = "%s secure a hard-fought 2-1 away victory over %s at %s. Despite a determined effort from the home side, the visitors held their nerve and saw out the match to collect all three points.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    case "1-3":
                        message = "%s celebrate a convincing 3-1 away win against %s. The visitors controlled the game for long periods, combining sharp attacking play with confident defending. It was another strong performance from %s under the guidance of %s.".formatted(awayTeam, homeTeam, awayTeam, manager);
                        break;

                    case "1-4", "1-5":
                        message = "%s thrilled their travelling supporters with an emphatic %s victory over %s at %s. Their attacking football proved too much for the hosts, who struggled to contain wave after wave of pressure. It was another memorable away performance and a fully deserved three points.".formatted(awayTeam, result, homeTeam, stadium);
                        break;

                    case "2-3":
                        message = "%s come out on top in an entertaining 3-2 away victory over %s. Both sides created plenty of chances in an action-packed encounter, but the visitors showed their quality when it mattered most. Despite late pressure from the hosts, %s held on to secure a memorable three points.".formatted(awayTeam, homeTeam, awayTeam);
                        break;

                    case "2-4":
                        message = "%s delight their travelling supporters with an impressive 4-2 victory over %s at %s. It was an open and entertaining contest, but the visitors consistently looked the more dangerous team in attack. Their clinical finishing proved to be the difference as they wrapped up another deserved away win.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    default:
                        int difference = awayGoals - homeGoals;
                        switch (difference) {
                            case 1:
                                message = "%s come out on top in a thrilling %s away victory over %s. It was an end-to-end contest filled with attacking football, with both sides creating plenty of chances throughout the match. In the end, %s held their nerve to edge out the hosts and secure a dramatic three points on the road.".formatted(awayTeam, result, homeTeam, awayTeam);
                                break;

                            case 2:
                                message = "%s secure an entertaining %s away victory over %s. Both teams showed plenty of attacking intent and found the back of the net several times, but the visitors consistently had the edge going forward. It was another exciting performance away from home that earned %s a well-deserved three points.".formatted(awayTeam, result, homeTeam, awayTeam);
                                break;

                            case 3:
                                message = "%s produce an excellent display to beat %s %s away from home. The visitors controlled the game from the early stages and combined attacking quality with disciplined defending to stay comfortably ahead throughout the match. It was another deserved victory that showcased the team's strength from start to finish under %s.".formatted(awayTeam, homeTeam, result, manager);
                                break;

                            case 4, 5, 6:
                                message = "%s put on an unforgettable display to beat %s %s away from home. Their attacking quality was simply too much for the hosts, who struggled to cope throughout the match despite finding the back of the net. It was another outstanding performance under the guidance of %s, whose side looked unstoppable from the opening whistle.".formatted(awayTeam, homeTeam, result, manager);
                                break;
                        }
                }
            } else if (homeGoals > awayGoals) {
                switch (result) {
                    case "1-0":
                        message = "%s suffer a narrow 1-0 away defeat to %s. Both teams defended well throughout the match, with clear chances few and far between. In the end, %s found the only goal of the game, leaving the visitors empty-handed.".formatted(awayTeam, homeTeam, homeTeam);
                        break;

                    case "2-0":
                        message = "%s fall to a 2-0 away defeat against %s. Despite creating a few promising moments, the visitors struggled to break down the home defence at %s, while the hosts took their chances and secured a deserved victory.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    case "3-0":
                        message = "%s are beaten 3-0 away from home by %s after a difficult afternoon. The hosts proved clinical in front of goal and rarely looked troubled defensively. %s will be looking for a response from his side in the next match.".formatted(awayTeam, homeTeam, manager);
                        break;

                    case "4-0", "5-0", "6-0", "7-0":
                        message = "%s endure a heavy %s away defeat to %s. The hosts dominated from the opening whistle, leaving the visitors with little opportunity to get back into the contest. It was a disappointing performance for %s and his team.".formatted(awayTeam, result, homeTeam, manager);
                        break;

                    case "2-1":
                        message = "%s suffer a frustrating 2-1 away defeat to %s at %s. The visitors battled throughout the match and found the back of the net, but the hosts ultimately did enough to claim all three points.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    case "3-1":
                        message = "%s are defeated 3-1 away from home by %s. Although the visitors managed to score, they struggled to contain the hosts for long periods of the match. %s will be hoping his side can quickly put this result behind them.".formatted(awayTeam, homeTeam, manager);
                        break;

                    case "4-1", "5-1":
                        message = "%s leave %s with a disappointing %s defeat against %s. The hosts looked dangerous every time they attacked and punished the visitors throughout the match. It was a difficult afternoon from start to finish.".formatted(awayTeam, stadium, result, homeTeam);
                        break;

                    case "3-2":
                        message = "%s come up just short in an entertaining 3-2 away defeat to %s. It was an open contest with chances at both ends, but the hosts proved slightly more clinical when it mattered most. Despite a spirited effort, the visitors returned home without any points.".formatted(awayTeam, homeTeam);
                        break;

                    case "4-2":
                        message = "%s are beaten 4-2 by %s at %s in an entertaining encounter. The visitors showed flashes of quality going forward, but defensive mistakes proved costly as the hosts secured a deserved victory.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    default:
                        int difference = homeGoals - awayGoals;
                        switch (difference) {
                            case 1:
                                message = "%s fall to a narrow %s away defeat against %s. It was an end-to-end contest with both sides creating plenty of opportunities, but the hosts made the decisive moments count. Despite a determined effort, the visitors were unable to rescue a result.".formatted(awayTeam, result, homeTeam);
                                break;

                            case 2:
                                message = "%s suffer a disappointing %s away defeat to %s. Both teams looked dangerous going forward and found the back of the net on several occasions, but the hosts consistently had the edge in attack. The visitors were left with plenty to reflect on after the final whistle.".formatted(awayTeam, result, homeTeam);
                                break;

                            case 3:
                                message = "%s are comfortably beaten %s away from home by %s. The hosts controlled the match from the early stages, combining clinical finishing with a disciplined defensive display. It was a deserved victory for %s from start to finish.".formatted(awayTeam, result, homeTeam, homeTeam);
                                break;

                            case 4, 5, 6:
                                message = "%s endure a heavy %s away defeat against %s. The hosts proved far too strong throughout the match, dominating the game with relentless attacking football while giving the visitors little chance to recover. %s will be hoping his side can quickly bounce back from a difficult afternoon.".formatted(awayTeam, result, homeTeam, manager);
                                break;
                        }
                }
            } else {
                switch (result) {
                    case "0-0":
                        message = "%s earn a hard-fought point away from home after a goalless draw against %s at %s. Both sides remained well organized defensively throughout the match, making clear-cut chances difficult to create. In the end, neither team could find the breakthrough as they settled for a point each.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    case "1-1":
                        message = "%s and %s share the spoils in an entertaining 1-1 draw. The visitors enjoyed plenty of positive moments going forward and managed to find the back of the net, but neither side could produce the decisive goal. %s will take positives from the performance while knowing there was still room for improvement.".formatted(awayTeam, homeTeam, manager);
                        break;

                    case "2-2":
                        message = "%s battle to an exciting 2-2 draw away to %s at %s. The match was played at a high tempo, with both sides showing attacking intent and responding well whenever they fell behind. In the end, the visitors earned a well-deserved point on the road.".formatted(awayTeam, homeTeam, stadium);
                        break;

                    case "3-3", "4-4", "5-5", "6-6", "7-7":
                        message = "%s and %s produce a spectacular %s draw in one of the most entertaining matches of the season. The crowd at %s witnessed relentless attacking football, countless chances and dramatic momentum swings throughout the contest. Neither side could find the decisive goal, but %s returned home with a well-earned point after an unforgettable encounter.".formatted(awayTeam, homeTeam, result, stadium, awayTeam);
                        break;
                }
            }
        }
        return message;
    }
}
