package bg.softuni.matchday.scheduler;


import bg.softuni.matchday.game.service.GameService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameProcessingScheduler {

    private final GameService gameService;

    public GameProcessingScheduler(GameService gameService) {
        this.gameService = gameService;
    }

    @Scheduled(fixedRate = 60000)
    public void processGames() {
        gameService.processGame();
    }
}