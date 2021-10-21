package ru.sberbankschool.restaurantcustomers.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramFacade {

    private GoogleSheetsService googleSheetsService;
    private final DbService dbService;

    @Autowired
    public TelegramFacade(DbService dbService, GoogleSheetsService googleSheetsService) {
        this.dbService = dbService;
        this.googleSheetsService = googleSheetsService;
    }


    public BotApiMethod<?> handleUpdate(Update update) {

        SendMessage sendMessage = null;

        if (update.getMessage() != null && update.getMessage().hasEntities()) {
            Message message = update.getMessage();
            sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            Optional<MessageEntity> commandEntity = message
                    .getEntities()
                    .stream()
                    .filter(e -> "bot_command".equals(e.getType()))
                    .findFirst();
            if (commandEntity.isPresent()) {
                String command = message
                        .getText()
                        .substring(
                                commandEntity.get().getOffset(),
                                commandEntity.get().getLength()
                        );
                switch (command) {
                    case "/getcard": {
                        if (message.getText().split(" ").length == 2) {
                            Long phoneNumber = null;
                            Customer customer = null;
                            try {
                                phoneNumber = Long.valueOf(message.getText().split(" ")[1]);
                                customer = dbService.getCustomerFromDataBase(phoneNumber);
                            } catch (NumberFormatException e) {
                                sendMessage.setText("Клиент не найден!");
                                return sendMessage;
                            }
                            if (customer == null)
                                customer = googleSheetsService.findCustomerByPhoneNumber(phoneNumber);
                            if (customer == null) {
                                sendMessage.setText("Клиент не найден!");
                                return sendMessage;
                            }
                            sendMessage.setText(customer.toString());
                            return sendMessage;
                        } else if (message.getText().split(" ").length == 4) {
                            String lastName = message.getText().split(" ")[1];
                            String firstName = message.getText().split(" ")[2];
                            String middleName = message.getText().split(" ")[3];
                            List<Customer> customers = dbService.getCustomerByNameFromDataBase(
                                    lastName, firstName, middleName
                            );
                            if (customers.isEmpty() || customers == null)
                                customers = googleSheetsService.findCustomersByName(
                                        lastName, firstName, middleName
                                );
                            if (customers == null || customers.isEmpty()) {
                                sendMessage.setText("Клиент не найден!");
                                return sendMessage;
                            }

//                            sendMessage = customerBuilder(sendMessage.getChatId(), customers);
                            return customerBuilder(sendMessage.getChatId(), customers);
                        } else {
                            sendMessage.setText("Неверный формат команды");
                            return sendMessage;
                        }
                    }
                    default: {
                        sendMessage.setText("Команда не найдена!");
                        return sendMessage;
                    }
                }
            }
        }
        return sendMessage;
    }

    public BotApiMethod<?> customerBuilder(String chatId, List<Customer> list) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        StringBuilder builder = new StringBuilder();
        if (list.isEmpty()) {
            replyMessage.setText("Клиент не найден!");
            return replyMessage;
        }
        for (Customer customer : list) {
            builder.append(buildCustomer(customer)).append('\n').append('\n');
        }
        replyMessage.setText(builder.toString());
        return replyMessage;
    }

    private StringBuilder buildCustomer(Customer customer) {
        StringBuilder builder = new StringBuilder();
        builder.append(customer.toString());
        return builder;
    }
}
