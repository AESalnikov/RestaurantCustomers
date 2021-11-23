package ru.sberbankschool.restaurantcustomers.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sberbankschool.restaurantcustomers.handler.CallbackQueryHandler;
import ru.sberbankschool.restaurantcustomers.handler.CommandHandler;
import ru.sberbankschool.restaurantcustomers.constants.Step;

@Component
public class TelegramFacade {

    public static Step step = Step.START;
    private final CommandHandler commandHandle;
    private final CallbackQueryHandler callbackQueryHandler;

    public TelegramFacade(CommandHandler commandHandler, CallbackQueryHandler callbackQueryHandler) {
        this.commandHandle = commandHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }

    public BotApiMethod<?> updateHandler(Update update) {

        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.handler(update.getCallbackQuery());
        }

        if (update.getMessage() != null && update.getMessage().hasEntities()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            String command = commandHandle.getCommand(message);
            return commandHandle.chooseCommand(command, message, sendMessage);
        }
        return null;
    }
}