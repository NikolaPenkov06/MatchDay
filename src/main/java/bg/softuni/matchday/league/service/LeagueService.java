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

    public void addLeagues(){
        leagueRepository.save(League.builder()
                .name("Premier League")
                .logo("https://loodibee.com/wp-content/uploads/Premier-League-Logo.png")
                .country(Country.ENGLAND)
                .teams(new ArrayList<>())
                .build());
        leagueRepository.save(League.builder()
                .name("La Liga")
                .logo("https://logos-world.net/wp-content/uploads/2023/07/LaLiga-Logo.png")
                .country(Country.SPAIN)
                .teams(new ArrayList<>())
                .build());
        leagueRepository.save(League.builder()
                .name("Serie A")
                .logo("https://brandlogos.net/wp-content/uploads/2021/12/serie_a-brandlogo.net_-512x512.png")
                .country(Country.ITALY)
                .teams(new ArrayList<>())
                .build());
        leagueRepository.save(League.builder()
                .name("Bundesliga")
                .logo("https://upload.wikimedia.org/wikipedia/en/thumb/d/df/Bundesliga_logo_%282017%29.svg/1200px-Bundesliga_logo_%282017%29.svg.png")
                .country(Country.GERMANY)
                .teams(new ArrayList<>())
                .build());
        leagueRepository.save(League.builder()
                .name("Ligue 1")
                .logo("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a0/Ligue_1_2024_Logo.png/800px-Ligue_1_2024_Logo.png")
                .country(Country.FRANCE)
                .teams(new ArrayList<>())
                .build());
    }

}
