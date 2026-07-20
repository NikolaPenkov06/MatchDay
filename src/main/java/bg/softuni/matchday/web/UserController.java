package bg.softuni.matchday.web;

import bg.softuni.matchday.game.service.GameService;
import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.Role;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
public class UserController {
    private final TeamService teamService;
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public UserController(TeamService teamService, UserService userService, GameService gameService) {
        this.teamService = teamService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/edit-roles")
    public ModelAndView getEditRolesPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        gameService.processGame();
        User user = userService.getById(authenticationDetails.getUserId());
        String position = teamService.getTeamPosition(user.getFavouriteTeam());
        List<User> users = userService.getAllUsers();
        users.sort(Comparator.comparing((User u) -> u.getRole() == Role.ADMIN ? 0 : 1).thenComparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER).thenComparing(User::getLastName, String.CASE_INSENSITIVE_ORDER));
        ModelAndView modelAndView = new ModelAndView("edit-roles");
        modelAndView.addObject("user", user);
        modelAndView.addObject("position", position);
        modelAndView.addObject("latestMatch", teamService.getLastMatchDetails(user.getFavouriteTeam()));
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @PostMapping("/edit-roles/make/{id}")
    public String makeAdmin(@PathVariable UUID id){
        userService.makeAdmin(id);
        return "redirect:/edit-roles";
    }

    @PostMapping("/edit-roles/remove/{id}")
    public String removeAdmin(@PathVariable UUID id){
        userService.removeAdmin(id);
        return "redirect:/edit-roles";
    }

    @PostMapping("/email-preference/{id}")
    public String changeEmailPreference(@PathVariable UUID id, @RequestHeader(value = "Referer", required = false) String referer) {

        userService.changeEmailPreference(id);

        return "redirect:" + (referer != null ? referer : "/");
    }
}
