package by.resliv.traveladvisor;

import by.resliv.traveladvisor.configuration.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class TravelAdvisorApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(new Class[]{TravelAdvisorApplication.class, AppConfig.class}, args);
    }
}
