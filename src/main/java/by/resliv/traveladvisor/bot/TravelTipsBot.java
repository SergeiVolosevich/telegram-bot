package by.resliv.traveladvisor.bot;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
public class TravelTipsBot extends TelegramLongPollingBot {

    private static final String FAILED_TO_SEND_RESPONSE = "Failed to send response. Cause:";
    private final Map<HandlerType, InputHandler> handlers;

    public TravelTipsBot(List<InputHandler> handlers) {
        this.handlers = handlers.stream().collect(Collectors.toMap(InputHandler::getCommandType, handler -> handler));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (Objects.nonNull(update)) {
            log.info("Get message with message - {}", update.getMessage());
            Message message = update.getMessage();
            InputHandler handler = getHandler(message);
            BotApiMethod<Message> response = handler.handle(message);
            sendResponse(response);
        }
    }

    private void sendResponse(BotApiMethod<Message> response) {
        try {
            execute(response);
        } catch (TelegramApiException e) {
            log.error(FAILED_TO_SEND_RESPONSE, e);
        }
    }

    private InputHandler getHandler(Message message) {
        if (message.hasText()) {
            String messageText = message.getText();
            if (messageText.startsWith("/")) {
                HandlerType handlerType = Stream.of(HandlerType.values())
                        .filter(HandlerType::isCommand)
                        .filter(type -> messageText.startsWith(type.getName()))
                        .findFirst()
                        .orElse(HandlerType.NO_SUCH_COMMAND);
                return handlers.get(handlerType);
            }
            return handlers.get(HandlerType.TEXT);
        }
        return handlers.get(HandlerType.NO_TEXT);
    }

    @Override
    public String getBotUsername() {
        return "travel_tips_task_bot";
    }

    @Override
    public String getBotToken() {
        return "1271332002:AAF6_UStKJ5LTbZn7Ir96FKUsUmaPUmnzZU";
    }

}
