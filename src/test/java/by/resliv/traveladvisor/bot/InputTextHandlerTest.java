package by.resliv.traveladvisor.bot;

import by.resliv.traveladvisor.dto.CityDTO;
import by.resliv.traveladvisor.service.CityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InputTextHandlerTest {

    @InjectMocks
    private InputTextHandler handler;

    @Mock
    private CityService cityService;

    @Mock
    private MessageSource messageSource;

    @Test
    void handle_ValidMessageWithLocale_ShouldReturnResponseWithOneCityInfo() {
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);
        List<CityDTO> cities = new ArrayList<>();
        cities.add(CityDTO.builder().id(1L).name("Minsk").description("City").build());

        when(mockMessage.getText()).thenReturn("Minsk");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getLanguageCode()).thenReturn("ru");
        when(cityService.findCityByName("Minsk")).thenReturn(cities);

        BotApiMethod<Message> response = handler.handle(mockMessage);

        assertNotNull(response);
        String expected = "Minsk:\nCity";
        String actual = ((SendMessage) response).getText();
        assertEquals(expected, actual);
    }

    @Test
    void handle_ValidMessageWithoutLocale_ShouldReturnResponse() {
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);
        List<CityDTO> cities = new ArrayList<>();
        cities.add(CityDTO.builder().id(1L).name("Minsk").description("City").build());

        when(mockMessage.getText()).thenReturn("Minsk");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getLanguageCode()).thenReturn(null);
        when(cityService.findCityByName("Minsk")).thenReturn(cities);

        BotApiMethod<Message> response = handler.handle(mockMessage);

        assertNotNull(response);
        String expected = "Minsk:\nCity";
        String actual = ((SendMessage) response).getText();
        assertEquals(expected, actual);
    }

    @Test
    void handle_InvalidValidMessage_ShouldReturnResponse() {
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);

        when(mockMessage.getText()).thenReturn("12314354");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getLanguageCode()).thenReturn("ru");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Enter the correct city name.");

        BotApiMethod<Message> response = handler.handle(mockMessage);

        assertNotNull(response);
        String expected = "Enter the correct city name.";
        String actual = ((SendMessage) response).getText();
        assertEquals(expected, actual);
    }

    @Test
    void handle_ValidMessage_ShouldReturnResponseWithoutCityInfo() {
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);
        List<CityDTO> cities = new ArrayList<>();

        when(mockMessage.getText()).thenReturn("Minsk");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getLanguageCode()).thenReturn("ru");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("I can not give information on this request. Try request information about another city.");
        when(cityService.findCityByName("Minsk")).thenReturn(cities);

        BotApiMethod<Message> response = handler.handle(mockMessage);

        assertNotNull(response);
        String expected = "I can not give information on this request. Try request information about another city.";
        String actual = ((SendMessage) response).getText();
        assertEquals(expected, actual);
    }

    @Test
    void handle_ValidMessage_ShouldReturnResponseWithSomeCityInfo() {
        Message mockMessage = mock(Message.class);
        User mockUser = mock(User.class);
        List<CityDTO> cities = new ArrayList<>();
        cities.add(CityDTO.builder().id(1L).name("Minsk").description("Minsk City").build());
        cities.add(CityDTO.builder().id(2L).name("Moscow").description("Moscow City").build());

        when(mockMessage.getText()).thenReturn("Minsk");
        when(mockMessage.getFrom()).thenReturn(mockUser);
        when(mockUser.getLanguageCode()).thenReturn("ru");
        when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenReturn("Perhaps you meant one of the following cities:\nMinsk:\nMinsk City\nMoscow:\nMoscow City");
        when(cityService.findCityByName("Minsk")).thenReturn(cities);

        BotApiMethod<Message> response = handler.handle(mockMessage);

        assertNotNull(response);
        String expected = "Perhaps you meant one of the following cities:\nMinsk:\nMinsk City\nMoscow:\nMoscow City";
        String actual = ((SendMessage) response).getText();
        assertEquals(expected, actual);
    }
}