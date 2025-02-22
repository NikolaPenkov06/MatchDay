package bg.softuni.matchday.game.model;

import jakarta.persistence.*;
import lombok.*;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.team.model.Team;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    @ManyToOne
    private League league;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private int homeCorners;

    @Column
    private int awayCorners;

    @Column
    private int homeFouls;

    @Column
    private int awayFouls;

    @Column
    private int homeOffsides;

    @Column
    private int awayOffsides;

    @Column
    private int homePossessionPercentage;

    @Column
    private int awayPossessionPercentage;

    @Column
    private int homeShotsOnTarget;

    @Column
    private int awayShotsOnTarget;

    @Column
    private int homeShotsOffTarget;

    @Column
    private int awayShotsOffTarget;





}
