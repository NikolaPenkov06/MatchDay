package bg.softuni.matchday.web;

import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.web.dto.LoginRequest;
import bg.softuni.matchday.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping
public class IndexController {


    private final TeamService teamService;

    @Autowired
    public IndexController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/")
    public ModelAndView getIndexPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        List<String> teamNames = teamService.getAllTeamsNames();
        modelAndView.addObject("teamNames", teamNames);
        return modelAndView;
    }
}
