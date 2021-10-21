package ru.sberbankschool.restaurantcustomers.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sberbankschool.restaurantcustomers.config.SheetsConfig;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Sheet;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleSheetsService {
    private final String applicationName;
    private final String spreadsheetId;
    private final String range;
    private final String key;

    public GoogleSheetsService(SheetsConfig sheetsConfig) {
        this.applicationName = sheetsConfig.getApplicationName();
        this.spreadsheetId = sheetsConfig.getSpreadsheetId();
        this.range = sheetsConfig.getRange();
        this.key = sheetsConfig.getKey();
    }

    private String buildUrl() {
        return new StringBuilder(
                "https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetId +
                        "/values/" + applicationName + range +
                        "?key=" + key
        ).toString();
    }

    public List<Customer> getValues() {
        RestTemplate rest = new RestTemplate();
        String sheetLink = buildUrl();
        ResponseEntity<Sheet> responseEntity = rest.getForEntity(sheetLink, Sheet.class);
        List<List<String>> values = responseEntity.getBody().getValues();
        values.remove(0);
        return values.stream().map(Customer::new).collect(Collectors.toList());
    }

    public Customer findCustomerByPhoneNumber(long phoneNumber) {
        List<Customer> customers = getValues();
        customers = customers
                .stream()
                .filter(
                        customer -> customer.getPhoneNumber() == phoneNumber
                )
                .collect(Collectors.toList());
        return customers.isEmpty() ? null : customers.get(0);
    }

    public List<Customer> findCustomersByName(String lastName, String firstName, String secondName) {
        List<Customer> customers = getValues();
        customers = customers
                .stream()
                .filter(
                        customer -> customer.getLastName().equals(lastName) &&
                                customer.getFirstName().equals(firstName) &&
                                customer.getSecondName().equals(secondName)
                )
                .collect(Collectors.toList());
        return customers.isEmpty() ? null : customers;
    }
}
