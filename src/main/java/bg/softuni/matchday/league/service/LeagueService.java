package bg.softuni.matchday.league.service;

import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.league.repository.LeagueRepository;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.user.model.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @Cacheable("leagues")
    public List<League> getAllLeagues(){
        return leagueRepository.findAll();
    }

    @Cacheable(value = "leaguesByName", key = "#name")
    public League findByName(String name){
        return leagueRepository.findByName(name).get();
    }

    @Cacheable(value = "leaguesById", key = "#id")
    public League findById(UUID id){
        return leagueRepository.findById(id).get();
    }

    public String getHeaderUpcoming(League league) {
        StringBuilder header = new StringBuilder();
        header.append("Upcoming matches in ");
        header.append(league.getName());

        return header.toString();
    }

    public String getHeaderLatest(League league) {
        StringBuilder header = new StringBuilder();
        header.append("Latest matches in ");
        header.append(league.getName());

        return header.toString();
    }

}
