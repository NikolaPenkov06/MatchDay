package bg.softuni.matchday.team.service;

import bg.softuni.matchday.league.repository.LeagueRepository;
import bg.softuni.matchday.team.model.Level;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<String> getAllTeamsNames() {

        return teamRepository.findAll().stream()
                .map(Team::getName)
                .collect(Collectors.toList());

    }

}
