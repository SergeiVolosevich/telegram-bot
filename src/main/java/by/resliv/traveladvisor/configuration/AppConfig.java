package by.resliv.traveladvisor.configuration;

import by.resliv.traveladvisor.bot.InputHandler;
import by.resliv.traveladvisor.bot.TravelTipsBot;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.List;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
@PropertySource("classpath:telegram.properties")
public class AppConfig {

    @Value("${telegram.name}")
    private String name;
    @Value("${telegram.token}")
    private String token;
    @Value("${telegram.thead}")
    private int threadCount;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        return mapper;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public TravelTipsBot travelTipsBot(List<InputHandler> inputHandlers) {
        DefaultBotOptions botOptions =new DefaultBotOptions();
        botOptions.setMaxThreads(threadCount);
        return new TravelTipsBot(inputHandlers, name, token, botOptions);
    }
}
