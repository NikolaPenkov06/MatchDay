package bg.softuni.matchday.config;

import bg.softuni.matchday.security.AuthenticationDetails;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final UserService userService;

    @Autowired
    public WebMvcConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(matchers -> matchers
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/register").permitAll()
                        .requestMatchers("/edit-roles/**").hasRole("ADMIN")
                        .requestMatchers("/add-match/**").hasRole("ADMIN")
                        .requestMatchers("/add-article/**").hasRole("ADMIN")
                        .requestMatchers("/home").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(404);
                            request.getRequestDispatcher("/not-found").forward(request, response);
                        })
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByUsername(username);
            return new AuthenticationDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole(),
                    user.isActive()
            );
        };
    }
}
