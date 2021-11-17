package ru.sberbankschool.restaurantcustomers.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DaoService;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;
import ru.sberbankschool.restaurantcustomers.utilites.MessageUtils;

@Component
public class CustomerHandler {

    private final DaoService dbService;
    private final GoogleSheetsService googleSheetsService;
    private final RatingHandler ratingHandler;

    public CustomerHandler(DaoService dbService, GoogleSheetsService googleSheetsService, RatingHandler ratingHandler) {
        this.dbService = dbService;
        this.googleSheetsService = googleSheetsService;
        this.ratingHandler = ratingHandler;
    }

    public SendMessage getCustomerFromDb(Message message) {
        String searchItem = message.getText().split(" ")[1];
        Customer customer;
        try {
            customer = dbService.getCustomerByPhoneNumber(Long.parseLong(searchItem));
        } catch (NumberFormatException e) {
            customer = dbService.getCustomerByEmail(searchItem);
        }
        if (customer != null)
            dbService.saveCustomer(customer);
        return customer == null ? null : MessageUtils.createCustomersCard(
                customer,
                message.getChatId().toString(),
                ratingHandler.getRating(customer),
                ratingHandler.getTips(customer)
        );
    }

    public SendMessage getCustomerFromGoogleSheets(Message message) {
        String searchItem = message.getText().split(" ")[1];
        Customer customer;
        try {
            customer = googleSheetsService.findCustomerByPhoneNumber(Long.parseLong(searchItem));
        } catch (NumberFormatException e) {
            customer = googleSheetsService.findCustomerByEmail(searchItem);
        }
        if (customer != null)
            dbService.saveCustomer(customer);
        return customer == null ? MessageUtils.clientNotFound(message)
                : MessageUtils.createCustomersCard(
                customer,
                message.getChatId().toString(),
                ratingHandler.getRating(customer),
                ratingHandler.getTips(customer)
        );
    }
}
