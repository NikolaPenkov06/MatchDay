package bg.softuni.matchday.web;

import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.LoginRequest;
import bg.softuni.matchday.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping
public class IndexController {


    private final TeamService teamService;
    private final UserService userService;

    @Autowired
    public IndexController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getIndexPage(){

        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session){

        ModelAndView modelAndView = new ModelAndView("login");

        if(bindingResult.hasErrors()){
            return modelAndView;
        }

        User loggedInUser = userService.login(loginRequest);

        if(loggedInUser == null){
            boolean wrongCredentials = true;
            modelAndView.addObject("wrongCredentials", wrongCredentials);
            return modelAndView;
        }

        session.setAttribute("user_id", loggedInUser.getId());

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        modelAndView.addObject("passwordsMatch", true);

        getListOfTeamNames(modelAndView);
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult){

        ModelAndView modelAndView = new ModelAndView("register");

        System.out.println();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("registerRequest", registerRequest);

            return checkIfUsernameOrEmailIsInUse(registerRequest, modelAndView, true);

        }

        return checkIfUsernameOrEmailIsInUse(registerRequest, modelAndView, false);

    }

    public ModelAndView checkIfUsernameOrEmailIsInUse(RegisterRequest registerRequest, ModelAndView modelAndView, boolean isInBindingCheck) {

        modelAndView.addObject("passwordsMatch", userService.doPasswordsMatch(registerRequest));
        getListOfTeamNames(modelAndView);

        switch (userService.checkForTakenCredentials(registerRequest)){

            case "Username":
                modelAndView.addObject("takenUsername", true);
                return modelAndView;

            case "Email":
                modelAndView.addObject("takenEmail", true);
                return modelAndView;

            case "Username Email":
                modelAndView.addObject("takenEmail", true);
                modelAndView.addObject("takenUsername", true);
                return modelAndView;
            default:
                if(isInBindingCheck || !userService.doPasswordsMatch(registerRequest)){
                    return modelAndView;
                }
                userService.register(registerRequest);
                return new ModelAndView("redirect:/login");
        }

    }

    public void getListOfTeamNames(ModelAndView modelAndView) {
        List<String> teamNames = teamService.getAllTeamsNames();
        Collections.sort(teamNames);
        modelAndView.addObject("teamNames", teamNames);
    }


}
