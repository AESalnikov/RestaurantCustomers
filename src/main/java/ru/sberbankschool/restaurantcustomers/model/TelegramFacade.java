package ru.sberbankschool.restaurantcustomers.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sberbankschool.restaurantcustomers.dao.CustomerDao;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramFacade {

    private GoogleSheetsService googleSheetsService;
    private final CustomerDao dao;

    @Autowired
    public TelegramFacade(CustomerDao dao, GoogleSheetsService googleSheetsService) {
        this.dao = dao;
        this.googleSheetsService = googleSheetsService;
    }


    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.getMessage() != null && update.getMessage().hasEntities()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            String command = getCommand(message);
            return chooseCommand(command, message, sendMessage);
        }
        return null;
    }

    private BotApiMethod<?> getCustomerByFullName(Message message, SendMessage sendMessage) {
        String lastName = message.getText().split(" ")[1];
        String firstName = message.getText().split(" ")[2];
        String middleName = message.getText().split(" ")[3];
        List<Customer> customers = dao.findCustomerByFullName(
                lastName, firstName, middleName
        );
        if (customers == null || customers.isEmpty()) {
            customers = googleSheetsService.findCustomersByFullName(
                    lastName, firstName, middleName
            );
            if (customers == null || customers.isEmpty()) {
                return clientNotFound(sendMessage);
            }
            dao.saveAllCustomersFromGoogleSheet(customers);
        }
        return customersBuilder(sendMessage.getChatId(), customers);
    }

    private BotApiMethod<?> getCustomerByPhoneNumber(Message message, SendMessage sendMessage) {
        Long phoneNumber = null;
        Customer customer = null;
        try {
            phoneNumber = Long.valueOf(message.getText().split(" ")[1]);
            customer = dao.findCustomerByPhoneNumber(phoneNumber);
        } catch (NumberFormatException e) {
            return clientNotFound(sendMessage);
        }
        if (customer == null) {
            customer = googleSheetsService.findCustomerByPhoneNumber(phoneNumber);
            if (customer == null) {
                return clientNotFound(sendMessage);
            }
            dao.saveCustomer(customer);
        }
        sendMessage.setText(customer.toString());
        return sendMessage;
    }

    private BotApiMethod<?> customersBuilder(String chatId, List<Customer> list) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        StringBuilder builder = new StringBuilder();
        if (list.isEmpty()) {
            return clientNotFound(replyMessage);
        }
        for (Customer customer : list) {
            builder.append(customer.toString()).append('\n').append('\n');
        }
        replyMessage.setText(builder.toString());
        return replyMessage;
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
        switch (command) {
            case "/getcard": {
                if (message.getText().split(" ").length == 2) {
                    return getCustomerByPhoneNumber(message, sendMessage);
                } else if (message.getText().split(" ").length == 4) {
                    return getCustomerByFullName(message, sendMessage);
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

    private SendMessage clientNotFound(SendMessage sendMessage) {
        sendMessage.setText("Клиент не найден!");
        return sendMessage;
    }
}
