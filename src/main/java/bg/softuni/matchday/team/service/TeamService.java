package bg.softuni.matchday.team.service;

import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.repository.GameRepository;
import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.league.repository.LeagueRepository;
import bg.softuni.matchday.league.service.LeagueService;
import bg.softuni.matchday.team.model.Level;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.repository.TeamRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final LeagueService leagueService;

    @Autowired
    public TeamService(TeamRepository teamRepository, LeagueService leagueService) {
        this.teamRepository = teamRepository;
        this.leagueService = leagueService;
    }

    public List<String> getAllTeamsNames() {

        return teamRepository.findAll().stream()
                .map(Team::getName)
                .collect(Collectors.toList());

    }

    public String getTeamPosition(Team team){
        League league = team.getLeague();
        List<Team> teams = league.getTeams();
        teams.sort(Comparator.comparingInt(Team::getPoints).thenComparingInt(t -> t.getGoalsFor() - t.getGoalsAgainst()).reversed());
        int position = teams.indexOf(team) + 1;
        switch (position){
            case 1:
                return position + "st";

            case 2:
                return position + "nd";

            case 3:
                return position + "rd";

            default:
                return position + "th";
        }
    }

    public String getLastMatchDetails(Team team){
        List<Game> allMatches = team.getHomeGames();
        allMatches.addAll(team.getAwayGames());
        allMatches = allMatches.stream()
                .filter(game -> game.getStartTime().isBefore(LocalDateTime.now()))
                .sorted((g1, g2) -> g2.getStartTime().compareTo(g1.getStartTime()))
                .toList();
        if (allMatches.isEmpty()){
            return "No games played yet";
        }
        Game latestGame = allMatches.get(0);
        StringBuilder matchDetails = new StringBuilder();
        matchDetails.append(latestGame.getHomeGoals());
        matchDetails.append("-");
        matchDetails.append(latestGame.getAwayGoals());
        if (latestGame.getHomeTeam().equals(team)) {
            if(latestGame.getHomeGoals() > latestGame.getAwayGoals()){
                matchDetails.append(" win vs ");
            } else if(latestGame.getHomeGoals() < latestGame.getAwayGoals()){
                matchDetails.append(" loss vs ");
            } else {
                matchDetails.append(" draw vs ");
            }
            matchDetails.append(latestGame.getAwayTeam().getName());
        } else if (latestGame.getAwayTeam().equals(team)) {
            if(latestGame.getHomeGoals() < latestGame.getAwayGoals()){
                matchDetails.append(" win vs ");
            } else if(latestGame.getHomeGoals() > latestGame.getAwayGoals()){
                matchDetails.append(" loss vs ");
            } else {
                matchDetails.append(" draw vs ");
            }
            matchDetails.append(latestGame.getHomeTeam().getName());
        }


        return matchDetails.toString();
    }

    @Cacheable(value = "teamsByName", key = "#favouriteTeamName")
    public Team findByName(String favouriteTeamName) {
        return teamRepository.findByName(favouriteTeamName).get();
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public void resetStats(){
        List<Team> teams = teamRepository.findAllByLeagueName("Bundesliga");

        for (Team team : teams) {
            team.setGoalsAgainst(0);
            team.setMatchesPlayed(0);
            team.setGoalsFor(0);
            team.setWins(0);
            team.setDraws(0);
            team.setLosses(0);
            team.setPoints(0);
            team.setHomeGames(new ArrayList<>());
            team.setAwayGames(new ArrayList<>());
        }
    }

    public void updateTeams() {
        Team team = teamRepository.findByName("Empoli").get();
        team.setName("Sassuolo");
        team.setCity("Sassuolo");
        team.setFounded(LocalDate.of(1920, 3, 23));
        team.setLogo("https://upload.wikimedia.org/wikipedia/en/thumb/1/1c/US_Sassuolo_Calcio_logo.svg/800px-US_Sassuolo_Calcio_logo.svg.png");
        team.setStadiumName("Città del Tricolore");
        team.setCoachName("Fabio Grosso");
        teamRepository.save(team);

        team = teamRepository.findByName("Venezia").get();
        team.setName("Pisa");
        team.setCity("Pisa");
        team.setFounded(LocalDate.of(1909, 9, 24));
        team.setLogo("https://upload.wikimedia.org/wikipedia/en/thumb/6/6b/Pisa_SC_crest.svg/800px-Pisa_SC_crest.svg.png");
        team.setStadiumName("Alberto Gilardino");
        team.setCoachName("Arena Garibaldi");
        teamRepository.save(team);

        team = teamRepository.findByName("Monza").get();
        team.setName("Cremonese");
        team.setCity("Cremona");
        team.setFounded(LocalDate.of(1903, 3, 24));
        team.setLogo("https://upload.wikimedia.org/wikipedia/en/thumb/e/e1/US_Cremonese_logo.svg/1024px-US_Cremonese_logo.svg.png");
        team.setStadiumName("Stadio Giovanni Zini");
        team.setCoachName("Davide Nicola");
        teamRepository.save(team);

        team = teamRepository.findByName("Napoli").get();
        team.setLogo("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/SSC_Napoli_2025_%28white_and_azure%29.svg/1024px-SSC_Napoli_2025_%28white_and_azure%29.svg.png");
        teamRepository.save(team);

        team = teamRepository.findByName("Fiorentina").get();
        team.setLogo("https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/ACF_Fiorentina_-_logo_%28Italy%2C_2022%29.svg/1024px-ACF_Fiorentina_-_logo_%28Italy%2C_2022%29.svg.png");
        teamRepository.save(team);

        team = teamRepository.findByName("Juventus").get();
        team.setLogo("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Juventus_FC_-_logo_black_%28Italy%2C_2020%29.svg/800px-Juventus_FC_-_logo_black_%28Italy%2C_2020%29.svg.png");
        teamRepository.save(team);

        team = teamRepository.findByName("Como").get();
        team.setLogo("https://upload.wikimedia.org/wikipedia/commons/thumb/9/99/Calcio_Como_-_logo_%28Italy%2C_2019-%29.svg/1024px-Calcio_Como_-_logo_%28Italy%2C_2019-%29.svg.png");
        teamRepository.save(team);
    }

    public void updateManagers() {
        Team team = teamRepository.findByName("Tottenham Hotspur").get();
        team.setCoachName("Thomas Frank");
        teamRepository.save(team);

        team = teamRepository.findByName("Nottingham Forest").get();
        team.setCoachName("Sean Dyche");
        teamRepository.save(team);

        team = teamRepository.findByName("Brentford").get();
        team.setCoachName("Keith Andrews");
        teamRepository.save(team);

        team = teamRepository.findByName("West Ham United").get();
        team.setCoachName("Nuno Espírito Santo");
        teamRepository.save(team);

        team = teamRepository.findByName("West Ham United").get();
        team.setCoachName("Nuno Espírito Santo");
        teamRepository.save(team);

        team = teamRepository.findByName("Lecce").get();
        team.setCoachName("Eusebio Di Francesco");
        teamRepository.save(team);

        team = teamRepository.findByName("Fiorentina").get();
        team.setCoachName("Paolo Vanoli");
        teamRepository.save(team);

        team = teamRepository.findByName("Torino").get();
        team.setCoachName("Marco Baroni");
        teamRepository.save(team);

        team = teamRepository.findByName("Juventus").get();
        team.setCoachName("Igor Tudor");
        teamRepository.save(team);

        team = teamRepository.findByName("Lazio").get();
        team.setCoachName("Maurizio Sarri");
        teamRepository.save(team);


        team = teamRepository.findByName("Pisa").get();
        team.setCoachName("Alberto Gilardino");
        team.setStadiumName("Arena Garibaldi");

        team = teamRepository.findByName("Milan").get();
        team.setCoachName("Massimiliano Allegri");
        teamRepository.save(team);

        team = teamRepository.findByName("Inter").get();
        team.setCoachName("Cristian Chivu");
        teamRepository.save(team);

        team = teamRepository.findByName("Atalanta").get();
        team.setCoachName("Ivan Jurić");
        teamRepository.save(team);

        team = teamRepository.findByName("Roma").get();
        team.setCoachName("Gian Piero Gasperini");
        teamRepository.save(team);

        team = teamRepository.findByName("Cagliari").get();
        team.setCoachName("Fabio Pisacane");
        teamRepository.save(team);

        team = teamRepository.findByName("Parma").get();
        team.setCoachName("Carlos Cuesta");
        teamRepository.save(team);

        team = teamRepository.findByName("RB Leipzig").get();
        team.setCoachName("Ole Werner");
        teamRepository.save(team);

        team = teamRepository.findByName("Augsburg").get();
        team.setCoachName("Sandro Wagner");
        teamRepository.save(team);

        team = teamRepository.findByName("Werder Bremen").get();
        team.setCoachName("Horst Steffen");
        teamRepository.save(team);

        team = teamRepository.findByName("Wolfsburg").get();
        team.setCoachName("Paul Simonis");
        teamRepository.save(team);

        team = teamRepository.findByName("Bayer Leverkusen").get();
        team.setCoachName("Kasper Hjulmand");
        teamRepository.save(team);

        team = teamRepository.findByName("Nice").get();
        team.setCoachName("Franck Haise");
        teamRepository.save(team);

        team = teamRepository.findByName("Angers").get();
        team.setCoachName("Alexandre Dujeux");
        team.setStadiumName("Stade Raymond Kopa");

        team = teamRepository.findByName("Lens").get();
        team.setCoachName("Pierre Sage");
        teamRepository.save(team);

        team = teamRepository.findByName("Reims").get();
        team.setName("Metz");
        team.setCity("Metz");
        team.setFounded(LocalDate.of(1932, 3, 23));
        team.setLogo("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/FC_Metz_2021_Logo.svg/800px-FC_Metz_2021_Logo.svg.png");
        team.setStadiumName("Stade Saint-Symphorien");
        team.setCoachName("Stéphane Le Mignan");
        teamRepository.save(team);

        team = teamRepository.findByName("Rennes").get();
        team.setCoachName("Habib Beye");
        teamRepository.save(team);

        team = teamRepository.findByName("Nantes").get();
        team.setCoachName("Luís Castro");
        teamRepository.save(team);

        team = teamRepository.findByName("Lyon").get();
        team.setCoachName("Paulo Fonseca");
        teamRepository.save(team);

        team = teamRepository.findByName("Real Sociedad").get();
        team.setCoachName("Sergio Francisco");
        teamRepository.save(team);

        team = teamRepository.findByName("Osasuna").get();
        team.setCoachName("Alessio Lisci");
        teamRepository.save(team);

        team = teamRepository.findByName("Real Madrid").get();
        team.setCoachName("Xabi Alonso");
        teamRepository.save(team);

        team = teamRepository.findByName("Sevilla").get();
        team.setCoachName("Matías Almeyda");
        teamRepository.save(team);
    }

    @Cacheable(value = "teams", key = "#id")
    public Team getById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
    }

    public String getHeaderLatest(Team team) {
        StringBuilder header = new StringBuilder();
        header.append(team.getName());
        header.append("'s latest matches");

        return header.toString();
    }

    public String getHeaderUpcoming(Team team) {
        StringBuilder header = new StringBuilder();
        header.append(team.getName());
        header.append("'s upcoming matches");

        return header.toString();
    }

    @CacheEvict(value = "teams", key = "#team.id")
    public void save(Team team){
        teamRepository.save(team);
    }

}