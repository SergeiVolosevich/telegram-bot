package by.resliv.traveladvisor.bot;

import by.resliv.traveladvisor.dto.CityDTO;
import by.resliv.traveladvisor.service.CityService;
import by.resliv.traveladvisor.util.RequestUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InputTextHandler implements InputHandler {

    private static final String INVALID_CITY_NAME = "invalid.city.name";
    private static final String CITY_NOT_FOUND = "city.not.found";
    private static final String CITY_LIST = "city.list";

    private CityService cityService;
    private MessageSource messageSource;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String textMessage = message.getText();
        SendMessage response = RequestUtil.createResponseForBot(message);
        String locale = message.getFrom().getLanguageCode();
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault().toString();
        }
        Pattern pattern = Pattern.compile("^[а-яА-ЯёЁa-zA-Z]+(?:[\\s-][а-яА-ЯёЁa-zA-Z]+)*$");
        if (!pattern.asPredicate().test(textMessage)) {
            String reply = messageSource.getMessage(INVALID_CITY_NAME, null, new Locale(locale));
            response.setText(reply);
            return response;
        }
        List<CityDTO> cities = cityService.findCityByName(textMessage);
        if (cities.isEmpty()) {
            String reply = messageSource.getMessage(CITY_NOT_FOUND, null, new Locale(locale));
            response.setText(reply);
            return response;
        }
        if (cities.size() > 1) {
            String citiesInfo = cities.stream()
                    .map(city -> city.getName() + ":\n" + city.getDescription())
                    .collect(Collectors.joining("\n"));
            String reply = messageSource.getMessage(CITY_LIST, new Object[]{citiesInfo}, new Locale(locale));
            response.setText(reply);
            return response;
        }
        CityDTO city = cities.get(0);
        response.setText(city.getName() + ":\n" + city.getDescription());
        return response;
    }

    @Override
    public HandlerType getCommandType() {
        return HandlerType.TEXT;
    }
}
