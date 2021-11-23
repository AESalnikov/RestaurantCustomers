package ru.sberbankschool.restaurantcustomers.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.sberbankschool.restaurantcustomers.constants.Step;
import ru.sberbankschool.restaurantcustomers.utilites.MessageUtils;

import java.util.Optional;

import static ru.sberbankschool.restaurantcustomers.telegram.TelegramFacade.step;

@Component
public class CommandHandler {

    private final CustomerHandler customerHandler;

    public CommandHandler(CustomerHandler customerHandler) {
        this.customerHandler = customerHandler;
    }

    public String getCommand(Message message) {
        Optional<MessageEntity> commandEntity = message
                .getEntities()
                .stream()
                .filter(e -> "bot_command".equals(e.getType()))
                .findFirst();

        return commandEntity.map(messageEntity -> message
                .getText()
                .substring(
                        messageEntity.getOffset(),
                        messageEntity.getLength()
                )).orElse(null);
    }

    public BotApiMethod<?> chooseCommand(String command, Message message, SendMessage sendMessage) {
        if (step == Step.START) {
            if (command == null) {
                return MessageUtils.commandNotFound(message);
            }
            switch (command) {
                case "/help":
                    return MessageUtils.help(sendMessage);
                case "/getcard": {
                    if (message.getText().split(" ").length == 2) {
                        sendMessage = customerHandler.getCustomerFromDb(message);
                        if (sendMessage == null) {
                            sendMessage = customerHandler.getCustomerFromGoogleSheets(message);
                        }
                    } else {
                        sendMessage.setText("Неверный формат команды");
                        return sendMessage;
                    }
                    return sendMessage;
                }
                default: {
                    return MessageUtils.commandNotFound(message);
                }
            }
        } else {
            sendMessage.setText("Закончите опрос!");
            return sendMessage;
        }
    }
}
