package bg.softuni.matchday.user.service;

import bg.softuni.matchday.email.service.EmailService;
import bg.softuni.matchday.exception.UserDoesNotExistException;
import bg.softuni.matchday.league.model.League;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.Role;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.repository.UserRepository;
import bg.softuni.matchday.web.dto.LoginRequest;
import bg.softuni.matchday.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamService teamService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TeamService teamService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.teamService = teamService;
    }

    public User login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            throw new UserDoesNotExistException();
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return null;
        }

        return user;
    }


    public String checkForTakenCredentials(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());

        Optional<User> userOptionalEmail = userRepository.findByEmail(registerRequest.getEmail());

        if(userOptional.isPresent() && userOptionalEmail.isPresent()) {
            return "Username Email";
        } else if (userOptional.isPresent()) {
            return "Username";
        } else if (userOptionalEmail.isPresent()) {
            return "Email";
        }

        return "Nothing";
    }

    public void register(RegisterRequest registerRequest) {
        userRepository.save(initializeUser(registerRequest));
    }

    private User initializeUser(RegisterRequest registerRequest) {

        String profilePicture = registerRequest.getProfilePicture();

        if (profilePicture.isEmpty()) {
            profilePicture = "https://static.vecteezy.com/system/resources/previews/013/360/247/non_2x/default-avatar-photo-icon-social-media-profile-sign-symbol-vector.jpg";
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .country(registerRequest.getCountry())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .favouriteTeam(teamService.findByName(registerRequest.getFavouriteTeamName()))
                .profilePicture(profilePicture)
                .createdOn(LocalDateTime.now())
                .role(Role.USER)
                .isActive(true)
                .emailsEnabled(true)
                .build();

        return user;
    }

   public boolean doPasswordsMatch(RegisterRequest registerRequest){
       return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
   }

    @Cacheable(value = "users", key = "#userId")
    public User getById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Cacheable(value = "usersByUsername", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = {"users", "usersByUsername"}, allEntries = true)
    public void makeAdmin(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    @CacheEvict(value = {"users", "usersByUsername"}, allEntries = true)
    public void removeAdmin(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @CacheEvict(value = {"users", "usersByUsername"}, allEntries = true)
    public void changeEmailPreference(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        user.setEmailsEnabled(!user.isEmailsEnabled());
        userRepository.save(user);
    }


}
