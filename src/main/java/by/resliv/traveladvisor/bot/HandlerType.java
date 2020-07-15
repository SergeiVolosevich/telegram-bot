package by.resliv.traveladvisor.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HandlerType {
    TEXT("text", false), START_COMMAND("/start", true),
    HELP_COMMAND("/help", true), NO_TEXT("no text", false),
    NO_SUCH_COMMAND("no such command", false);

    private String name;
    private boolean isCommand;
}
