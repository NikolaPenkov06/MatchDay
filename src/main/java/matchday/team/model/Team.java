package matchday.team.model;

import jakarta.persistence.*;
import lombok.*;
import matchday.league.model.League;

import java.time.LocalDate;
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
