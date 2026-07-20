package bg.softuni.matchday.web;

import bg.softuni.matchday.email.service.EmailService;
import bg.softuni.matchday.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class EmailController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public EmailController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }
}
