package bg.softuni.matchday.web.dto;

import bg.softuni.matchday.team.model.Team;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMatchRequest {

    private UUID leagueId;

    private UUID homeTeamId;

    private UUID awayTeamId;

    @NotBlank(message = "Date is required")
    @Pattern(regexp = "^$|\\d{2}\\.\\d{2}\\.\\d{4}", message = "Date format must be dd.MM.yyyy")
    private String date;

    @NotBlank(message = "Time is required")
    @Pattern(regexp = "^$|\\d{2}:\\d{2}", message = "Time format must be HH:mm")
    private String time;

}
