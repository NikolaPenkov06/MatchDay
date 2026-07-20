package bg.softuni.matchday.team.model;

import bg.softuni.matchday.game.model.Game;
import jakarta.persistence.*;
import lombok.*;
import bg.softuni.matchday.league.model.League;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String logo;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String stadiumName;

    @Column(nullable = false)
    private LocalDate founded;

    @Column
    private String coachName;

    @ManyToOne
    private League league;

    @OrderBy("startTime DESC")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "homeTeam")
    private List<Game> homeGames;

    @OrderBy("startTime DESC")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "awayTeam")
    private List<Game> awayGames;

    @Column(nullable = false)
    private int matchesPlayed;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int draws;

    @Column(nullable = false)
    private int losses;

    @Column(nullable = false)
    private int goalsFor;

    @Column(nullable = false)
    private int goalsAgainst;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private Level level;
}
