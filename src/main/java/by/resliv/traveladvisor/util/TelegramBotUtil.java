package by.resliv.traveladvisor.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.Objects;

public final class TelegramBotUtil {

    private TelegramBotUtil() {
    }

    public static SendMessage createResponse(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    public static String getLocaleFromRequest(Message message) {
        String locale = message.getFrom().getLanguageCode();
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault().toString();
        }
        return locale;
    }
}
