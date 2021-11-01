package ru.sberbankschool.restaurantcustomers.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;
import ru.sberbankschool.restaurantcustomers.utilites.MessageUtils;

@Component
public class CustomerHandler {

    private DbService dbService;
    private GoogleSheetsService googleSheetsService;

    public CustomerHandler(DbService dbService, GoogleSheetsService googleSheetsService) {
        this.dbService = dbService;
        this.googleSheetsService = googleSheetsService;
    }

    public SendMessage getCustomerFromDb(Message message) {
        String searchItem = message.getText().split(" ")[1];
        Customer customer;
        try {
            customer = dbService.getCustomerByPhoneNumber(Long.valueOf(searchItem));
        } catch (NumberFormatException e) {
            customer = dbService.getCustomerByEmail(searchItem);
        }
        if (customer != null)
            dbService.saveCustomer(customer);
        return customer == null ? null : MessageUtils.createCustomersCard(
                customer,
                message.getChatId().toString(),
                new RatingHandler(dbService).getRating(customer),
                new RatingHandler(dbService).getTips(customer)
        );
    }

    public SendMessage getCustomerFromGoogleSheets(Message message) {
        String searchItem = message.getText().split(" ")[1];
        Customer customer;
        try {
            customer = googleSheetsService.findCustomerByPhoneNumber(Long.valueOf(searchItem));
        } catch (NumberFormatException e) {
            customer = googleSheetsService.findCustomerByEmail(searchItem);
        }
        if (customer != null)
            dbService.saveCustomer(customer);
        return customer == null ? MessageUtils.clientNotFound(message)
                : MessageUtils.createCustomersCard(
                customer,
                message.getChatId().toString(),
                new RatingHandler(dbService).getRating(customer),
                new RatingHandler(dbService).getTips(customer)
        );
    }
}
