package bg.softuni.matchday.league.repository;

import bg.softuni.matchday.league.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeagueRepository extends JpaRepository<League, UUID> {

    Optional<League> findByName(String name);

}
