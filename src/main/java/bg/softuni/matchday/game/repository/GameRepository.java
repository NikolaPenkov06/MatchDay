package bg.softuni.matchday.game.repository;

import bg.softuni.matchday.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

    List<Game> findAllByStartTimeAfter(LocalDateTime startTime);

    List<Game> findAllByStartTimeAfterAndHomeTeamName(LocalDateTime startTime, String homeTeamName);

    List<Game> findAllByStartTimeAfterAndAwayTeamName(LocalDateTime startTime, String awayTeamName);

    List<Game> findAllByStartTimeBeforeAndHomeTeamName(LocalDateTime startTime, String homeTeamName);

    List<Game> findAllByStartTimeBeforeAndAwayTeamName(LocalDateTime startTime, String awayTeamName);

    List<Game> findAllByStartTimeBefore(LocalDateTime startTimeBefore);

    List<Game> findAllByHomeTeamName(String homeTeamName);

    List<Game> findAllByAwayTeamName(String awayTeamName);
}
