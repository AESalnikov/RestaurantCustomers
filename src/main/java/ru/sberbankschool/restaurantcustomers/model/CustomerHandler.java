package ru.sberbankschool.restaurantcustomers.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CustomerHandler {

    private DbService dbService;

    public CustomerHandler(DbService dbService) {
        this.dbService = dbService;
    }

    public BotApiMethod<?> getCustomer(Message message, SendMessage sendMessage) {
        String searchItem = message.getText().split(" ")[1];
        Customer customer;
        try {
            customer = dbService.getCustomerByPhoneNumber(Long.valueOf(searchItem));
        } catch (NumberFormatException e) {
            customer = dbService.getCustomerByEmail(searchItem);
        }
        if (customer != null)
            dbService.saveCustomer(customer);
        return customer == null ? null : new MessageHandler().createCustomersCard(
                customer,
                sendMessage.getChatId(),
                new RatingHandler(dbService).getRating(customer),
                new RatingHandler(dbService).getTips(customer)
        );
    }
}
