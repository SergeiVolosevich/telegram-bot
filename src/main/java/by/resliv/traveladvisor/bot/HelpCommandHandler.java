package by.resliv.traveladvisor.bot;

import by.resliv.traveladvisor.util.RequestUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.Objects;

@Component
@AllArgsConstructor
public class HelpCommandHandler implements InputHandler {

    private static final String HELP_MESSAGE = "help.message";
    private MessageSource messageSource;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String locale = message.getFrom().getLanguageCode();
        SendMessage response = RequestUtil.createResponseForBot(message);
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault().toString();
        }
        String reply = messageSource.getMessage(HELP_MESSAGE, null, new Locale(locale));
        response.setText(reply);
        return response;
    }

    @Override
    public HandlerType getCommandType() {
        return HandlerType.HELP_COMMAND;
    }
}
