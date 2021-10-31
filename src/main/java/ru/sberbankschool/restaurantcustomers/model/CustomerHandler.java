package ru.sberbankschool.restaurantcustomers.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CustomerHandler {

    private DbService dbService;

    public CustomerHandler(DbService dbService) {
        this.dbService = dbService;
    }

    public double getTips(Customer customer) {
        List<String> tips = dbService.getTips(customer);
        int trueTips = tips.stream().filter(t -> Objects.equals(Boolean.valueOf(t), true)).collect(Collectors.toList()).size();
        double result = trueTips / (double) tips.size();
        return ((double) Math.round(result * 10) / 10) * 100;
    }

    public double getRating(Customer customer) {
        List<Integer> marks = dbService.getMarks(customer);
        int sum = 0;
        for (Integer mark: marks)
            sum += mark;
        double result = sum / (double) marks.size();
        return (double) Math.round(result * 10) / 10;
    }

    private SendMessage createCustomersCard(Customer customer, String chatId) {
        double percentTips = getTips(customer);
        double rating = getRating(customer);
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

    public BotApiMethod<?> getCustomer(Message message, SendMessage sendMessage) {
        String searchItem = message.getText().split(" ")[1];
        Customer customer;
        try {
            customer = dbService.getCustomerByPhoneNumber(Long.valueOf(searchItem));
        } catch (NumberFormatException e) {
            customer = dbService.getCustomerByEmail(searchItem);
        }
        if (customer == null) {
            try {
                customer = dbService.getCustomerByPhoneNumber(Long.valueOf(searchItem));
            } catch (NumberFormatException e) {
                customer = dbService.getCustomerByEmail(searchItem);
            }
            if (customer == null) {
                return clientNotFound(sendMessage);
            }
            dbService.saveCustomer(customer);
        }
        return createCustomersCard(customer, sendMessage.getChatId());
    }

    private SendMessage clientNotFound(SendMessage sendMessage) {
        sendMessage.setText("Клиент не найден!");
        return sendMessage;
    }
}
