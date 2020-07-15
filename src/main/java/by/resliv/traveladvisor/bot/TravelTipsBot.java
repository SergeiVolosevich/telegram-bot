package by.resliv.traveladvisor.bot;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.DefaultBotOptions;
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

@Log4j2
public class TravelTipsBot extends TelegramLongPollingBot {

    private static final String FAILED_TO_SEND_RESPONSE = "Failed to send response. Cause:";
    private static final String MESSAGE_FROM_TELEGRAM = "Message from telegram bot - {}";

    private final Map<HandlerType, InputHandler> handlers;
    private final String botUserName;
    private final String botToken;

    public TravelTipsBot(List<InputHandler> handlers, String botUserName, String botToken, DefaultBotOptions botOptions) {
        super(botOptions);
        this.handlers = handlers.stream().collect(Collectors.toMap(InputHandler::getCommandType, handler -> handler));
        this.botUserName = botUserName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (Objects.nonNull(update)) {
            log.info(MESSAGE_FROM_TELEGRAM, update.getMessage());
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
                HandlerType handlerType = getCommandHandler(messageText);
                return handlers.get(handlerType);
            }
            return handlers.get(HandlerType.TEXT);
        }
        return handlers.get(HandlerType.NO_TEXT);
    }

    private HandlerType getCommandHandler(String messageText) {
        return Stream.of(HandlerType.values())
                .filter(HandlerType::isCommand)
                .filter(type -> messageText.startsWith(type.getName()))
                .findFirst()
                .orElse(HandlerType.NO_SUCH_COMMAND);
    }

    @Override
    public String getBotUsername() {
        return this.botUserName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

}
