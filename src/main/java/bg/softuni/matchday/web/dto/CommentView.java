package bg.softuni.matchday.web.dto;

import bg.softuni.matchday.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CommentView {

    private UUID id;
    private User commenter;
    private String content;
    private LocalDateTime commentDate;
}