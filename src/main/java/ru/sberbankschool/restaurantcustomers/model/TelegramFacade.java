package ru.sberbankschool.restaurantcustomers.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;
import ru.sberbankschool.restaurantcustomers.status.Status;

import java.util.*;

@Component
public class TelegramFacade {

    private GoogleSheetsService googleSheetsService;
    private final DbService dbService;
    public static Status status = Status.START;

    public TelegramFacade(DbService dbService, GoogleSheetsService googleSheetsService) {
        this.dbService = dbService;
        this.googleSheetsService = googleSheetsService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            return new CallbackQueryHandler(dbService, googleSheetsService).handler(update.getCallbackQuery());
        }

        if (update.getMessage() != null && update.getMessage().hasEntities()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            String command = getCommand(message);
            return chooseCommand(command, message, sendMessage);
        }
        return null;
    }


    private String getCommand(Message message) {
        Optional<MessageEntity> commandEntity = message
                .getEntities()
                .stream()
                .filter(e -> "bot_command".equals(e.getType()))
                .findFirst();

        if (commandEntity.isPresent())
            return message
                    .getText()
                    .substring(
                            commandEntity.get().getOffset(),
                            commandEntity.get().getLength()
                    );
        return null;
    }

    private BotApiMethod<?> chooseCommand(String command, Message message, SendMessage sendMessage) {
        if (status.equals(Status.START)) {
            switch (command) {
                case "/help":
                    return help(sendMessage);
                case "/getcard": {
                    if (message.getText().split(" ").length == 2) {
                        sendMessage = (SendMessage) new CustomerHandler(dbService).getCustomer(message, sendMessage);
                        if (sendMessage == null) {
                            String searchItem = message.getText().split(" ")[1];
                            Customer customer = null;
                            try {
                                customer = googleSheetsService.findCustomerByPhoneNumber(Long.valueOf(searchItem));
                            } catch (NumberFormatException e) {
                                customer = googleSheetsService.findCustomerByEmail(searchItem);
                            }
                            if (customer == null) {
                                return new MessageHandler().clientNotFound(sendMessage);
                            }
                            return new MessageHandler().createCustomersCard(
                                    customer,
                                    message.getChatId().toString(),
                                    new RatingHandler(dbService).getRating(customer),
                                    new RatingHandler(dbService).getTips(customer)
                            );
                        }
                    } else {
                        sendMessage.setText("Неверный формат команды");
                        return sendMessage;
                    }
                    return sendMessage;
                }
                default: {
                    sendMessage.setText("Команда не найдена!");
                    return sendMessage;
                }
            }
        } else {
            sendMessage.setText("Закончите опрос!");
            return sendMessage;
        }
    }

    private SendMessage help(SendMessage sendMessage) {
        sendMessage.setText("Информация!\n\n" +
                "Бот для получения карточки гостя ресторана.\n\n" +
                "Чтобы получить карточку введите:\n" +
                "/getcard [номер телефона]\n" +
                "или\n" +
                "/getcard [адрес электронной почты]");
        return sendMessage;
    }
}