package ru.sberbankschool.restaurantcustomers.utilites;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sberbankschool.restaurantcustomers.entity.Customer;

@UtilityClass
public class MessageUtils {

    public SendMessage createCustomersCard(Customer customer, String chatId, double rating, double percentTips) {
        String customerInfo = customer.toString() +
                "\nЧаевые: " + percentTips + "%" +
                "\nРейтинг: " + rating;
        SendMessage customersCard = new SendMessage();
        customersCard.setChatId(chatId);
        customersCard.setText(customerInfo);
        customersCard.setReplyMarkup(KeyboardUtils.estimateKeyboard());
        return customersCard;
    }

    public SendMessage help(SendMessage sendMessage) {
        sendMessage.setText("Информация!\n\n" +
                "Бот для получения карточки гостя ресторана.\n\n" +
                "Чтобы получить карточку введите:\n" +
                "/getcard [номер телефона]\n" +
                "или\n" +
                "/getcard [адрес электронной почты]");
        return sendMessage;
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
