package ru.sberbankschool.restaurantcustomers.model;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sberbankschool.restaurantcustomers.entity.Customer;

public class MessageHandler {

    public SendMessage createCustomersCard(Customer customer, String chatId, double rating, double percentTips) {
        String customerInfo = new StringBuilder()
                .append(customer.toString())
                .append("\nЧаевые: " + percentTips + "%")
                .append("\nРейтинг: " + rating).toString();
        SendMessage customersCard = new SendMessage();
        customersCard.setChatId(chatId);
        customersCard.setText(customerInfo);
        customersCard.setReplyMarkup(new KeyBoard().createMainKeyBoard());
        return customersCard;
    }

    public SendMessage clientNotFound(SendMessage sendMessage) {
        sendMessage.setText("Клиент не найден!");
        return sendMessage;
    }
}
