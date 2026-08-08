package bg.softuni.matchday.user;

import bg.softuni.matchday.exception.UserDoesNotExistException;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.team.service.TeamService;
import bg.softuni.matchday.user.model.Country;
import bg.softuni.matchday.user.model.Role;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.repository.UserRepository;
import bg.softuni.matchday.user.service.UserService;
import bg.softuni.matchday.web.dto.LoginRequest;
import bg.softuni.matchday.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private UserService userService;

    @Test
    void whenUser_tryToLogIn_nonExistentUsername_thenThrowException() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonExistentUsername");

        when(userRepository.findByUsername("nonExistentUsername"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserDoesNotExistException.class,
                () -> userService.login(loginRequest)
        );
    }

    @Test
    void whenUser_tryToLogIn_wrongPassword_thenReturnNull() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("wrongPassword");

        User user = new User();
        user.setUsername("username");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("username"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        User result = userService.login(loginRequest);

        assertNull(result);
    }

    @Test
    void whenUser_logsInSuccessfully_returnUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");

        User user = new User();
        user.setUsername("username");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("username"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encodedPassword"))
                .thenReturn(true);

        User result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals(user, result);

    }

    @Test
    void whenUserRegister_withTakenEmailAndUsername_returnMessage(){
        User user = new User();
        user.setEmail("email");
        user.setUsername("username");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("email");
        registerRequest.setUsername("username");

        when(userRepository.findByUsername("username"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("email"))
                .thenReturn(Optional.of(user));

       String result = userService.checkForTakenCredentials(registerRequest);

       assertEquals("Username Email", result);

    }

    @Test
    void whenUserRegister_withTakenEmail_returnMessage(){
        User user = new User();
        user.setEmail("email");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("email");

        when(userRepository.findByEmail("email"))
                .thenReturn(Optional.of(user));

        String result = userService.checkForTakenCredentials(registerRequest);

        assertEquals("Email", result);

    }

    @Test
    void whenUserRegister_withTakenUsername_returnMessage(){
        User user = new User();
        user.setUsername("username");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");

        when(userRepository.findByUsername("username"))
                .thenReturn(Optional.of(user));

        String result = userService.checkForTakenCredentials(registerRequest);

        assertEquals("Username", result);

    }

    @Test
    void whenUserRegister_withoutTakenCredentials_returnMessage(){
        User user = new User();
        user.setUsername("username");
        user.setEmail("email");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("differentUsername");
        registerRequest.setEmail("differentEmail");

        when(userRepository.findByUsername("differentUsername"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("differentEmail"))
                .thenReturn(Optional.empty());

        String result = userService.checkForTakenCredentials(registerRequest);

        assertEquals("Nothing", result);

    }

    @Test
    void whenUserRegisters_thenUserIsSaved() {
        RegisterRequest request = new RegisterRequest();

        request.setUsername("username");
        request.setEmail("email@email.com");
        request.setPassword("password");
        request.setFirstName("Nikola");
        request.setLastName("Penkov");
        request.setCountry(Country.BULGARIA);
        request.setFavouriteTeamName("Arsenal");
        request.setProfilePicture("");

        Team team = new Team();

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(teamService.findByName("Arsenal"))
                .thenReturn(team);

        userService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void ifPasswordsMatch_returnTrue(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");

        boolean result = userService.doPasswordsMatch(registerRequest);

        assertTrue(result);

    }

    @Test
    void getById_returnsRightUser(){
        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setId(uuid);

        when(userRepository.findById(uuid))
                .thenReturn(Optional.of(user));

        User result = userService.getById(uuid);

        assertEquals(user, result);

    }

    @Test
    void findByUsername_returnsRightUser(){

        User user = new User();
        user.setUsername("username");

        when(userRepository.findByUsername("username"))
                .thenReturn(Optional.of(user));

        User result = userService.findByUsername("username");

        assertEquals(user, result);

    }

    @Test
    void getAllUsers_returnsAllUsers() {
        User user = new User();
        user.setUsername("username");

        User user2 = new User();
        user2.setUsername("username2");

        User user3 = new User();
        user3.setUsername("username3");

        when(userRepository.findAll())
                .thenReturn(List.of(user, user2, user3));

        List<User> result = userService.getAllUsers();

        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(user, user2, user3)));
    }

    @Test
    void makeAdmin_changesRoleToAdmin() {
        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setId(uuid);
        user.setRole(Role.USER);

        when(userRepository.findById(uuid))
                .thenReturn(Optional.of(user));

        userService.makeAdmin(uuid);

        assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void removeAdmin_changesRoleToUser() {
        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setId(uuid);
        user.setRole(Role.ADMIN);

        when(userRepository.findById(uuid))
                .thenReturn(Optional.of(user));

        userService.removeAdmin(uuid);

        assertEquals(Role.USER, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void changeEmailPreference_changesPreference() {
        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setId(uuid);
        user.setEmailsEnabled(false);

        boolean preference = user.isEmailsEnabled();

        when(userRepository.findById(uuid))
                .thenReturn(Optional.of(user));

        userService.changeEmailPreference(uuid);

        assertEquals(!preference, user.isEmailsEnabled());
        verify(userRepository).save(user);
    }
}
