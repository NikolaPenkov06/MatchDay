package bg.softuni.matchday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MatchDayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchDayApplication.class, args);
    }

}
