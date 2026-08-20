package bg.softuni.matchday.scheduler;

import bg.softuni.matchday.email.service.EmailService;
import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.repository.GameRepository;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LeagueResultsScheduler {

    private final UserService userService;
    private final EmailService emailService;
    private final GameService gameService;

    public LeagueResultsScheduler(UserService userService, EmailService emailService, GameService gameService) {
        this.userService = userService;
        this.emailService = emailService;
        this.gameService = gameService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendLeagueResults() {
        gameService.processGame();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);

        List<Game> games = gameService.getAllGamesInTimeWindow(yesterday, now);
        List<User> users = userService.getAllUsers();

        for (User user : users) {

            if (!user.isEmailsEnabled()) {
                continue;
            }

            List<Game> leagueGames = games.stream().filter(game -> game.getLeague().equals(user.getFavouriteTeam().getLeague())).toList();

            if (leagueGames.isEmpty()) {
                continue;
            }

            String subject = "Yesterday's " + user.getFavouriteTeam().getLeague().getName() + " results";

            StringBuilder body = new StringBuilder();

            body.append("Here are yesterday's results from ")
                    .append(user.getFavouriteTeam().getLeague().getName())
                    .append(":\n\n");

            for (Game game : leagueGames) {
                body.append(game.getHomeTeam().getName())
                        .append(" ")
                        .append(game.getHomeGoals())
                        .append("-")
                        .append(game.getAwayGoals())
                        .append(" ")
                        .append(game.getAwayTeam().getName())
                        .append("\n");
            }

            emailService.sendEmail(
                    user.getId(),
                    user.getEmail(),
                    subject,
                    body.toString()
            );
        }
    }
}
