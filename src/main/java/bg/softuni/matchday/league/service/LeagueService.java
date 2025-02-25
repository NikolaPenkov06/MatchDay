package bg.softuni.matchday.league.service;

import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.league.repository.LeagueRepository;
import bg.softuni.matchday.user.model.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

}
