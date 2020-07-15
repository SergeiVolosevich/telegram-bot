package by.resliv.traveladvisor.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputHandler {
    BotApiMethod<Message> handle(Message message);

    HandlerType getCommandType();
}
