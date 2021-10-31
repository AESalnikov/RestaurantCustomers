package ru.sberbankschool.restaurantcustomers.utilites;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.model.KeyBoard;

@UtilityClass
public class MessageUtils {

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

    public SendMessage clientNotFound(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Клиент не найден!");
        return sendMessage;
    }
    public SendMessage commandNotFound(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Команда не найдена!");
        return sendMessage;
    }
}
