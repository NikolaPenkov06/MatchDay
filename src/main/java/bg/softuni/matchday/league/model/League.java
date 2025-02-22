package bg.softuni.matchday.league.model;

import jakarta.persistence.*;
import lombok.*;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.user.model.Country;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String logo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    @OrderBy("points DESC")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "league")
    private Collection<Team> teams;
}
