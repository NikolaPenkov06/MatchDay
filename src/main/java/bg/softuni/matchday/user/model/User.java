package bg.softuni.matchday.user.model;

import jakarta.persistence.*;
import lombok.*;
import bg.softuni.matchday.team.model.Team;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String profilePicture;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Country country;

    @Column
    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @ManyToOne
    private Team favouriteTeam;

}
