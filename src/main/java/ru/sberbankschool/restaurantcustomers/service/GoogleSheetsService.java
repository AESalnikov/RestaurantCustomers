package ru.sberbankschool.restaurantcustomers.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.config.SheetsConfig;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Sheet;
import ru.sberbankschool.restaurantcustomers.handler.RatingHandler;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoogleSheetsService implements GoogleSheets {
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private final List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final String applicationName;
    private final String spreadsheetId;
    private final String credentialsFilePath;
    private final String serviceAdmin;
    private final DatabaseServiceImpl dbService;
    private final Sheets sheetsService;
    private final RatingHandler ratingHandler;

    public GoogleSheetsService(SheetsConfig sheetsConfig, DatabaseServiceImpl dbService, RatingHandler ratingHandler) throws GeneralSecurityException, IOException {
        this.applicationName = sheetsConfig.getApplicationName();
        this.spreadsheetId = sheetsConfig.getSpreadsheetId();
        this.credentialsFilePath = sheetsConfig.getCredentials();
        this.serviceAdmin = sheetsConfig.getServiceAdmin();
        this.sheetsService = createSheetsService(applicationName);
        this.dbService = dbService;
        this.ratingHandler = ratingHandler;
    }

    private Credential getCredentials(NetHttpTransport httpTransport) throws IOException, GeneralSecurityException {
        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(serviceAdmin)
                .setServiceAccountPrivateKeyFromP12File(new File(credentialsFilePath))
                .setServiceAccountScopes(scopes)
                .build();
    }

    private Sheets createSheetsService(String applicationName) throws IOException, GeneralSecurityException {

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
                .setApplicationName(applicationName)
                .build();
    }

    @Override
    public List<Customer> getValues() {
        ValueRange response = null;
        try {
            response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, applicationName)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Object>> values = Objects.requireNonNull(response).getValues();
        values.remove(0);
        return values.stream().map(Customer::new).collect(Collectors.toList());
    }

    @Override
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

    @Override
    public Customer findCustomerByEmail(String email) {
        List<Customer> customers = getValues();
        customers = customers
                .stream()
                .filter(
                        customer -> customer.getEmail().equals(email)
                )
                .collect(Collectors.toList());
        return customers.isEmpty() ? null : customers.get(0);
    }

    private List<Object> titleForGoogleSheet() {
        return Arrays.asList(
                "Номер телефона",
                "Имя",
                "Электронная почта",
                "Адрес",
                "Чаевые",
                "Рейтинг"
        );
    }

    private List<Object> parseCustomerForSheetsData(Customer customer) {
        List<Object> jCustomer = new ArrayList<>();
        jCustomer.add(customer.getPhoneNumber());
        jCustomer.add(customer.getName());
        jCustomer.add(customer.getEmail());
        jCustomer.add(customer.getAddress());
        jCustomer.add(String.valueOf(ratingHandler.getTips(customer)));
        jCustomer.add(String.valueOf(ratingHandler.getRating(customer)));
        return jCustomer;
    }


    private Sheet prepareSheetData() {
        List<Customer> customers = dbService.getAllCustomers();
        Sheet sheet = new Sheet();
        sheet.setRange(applicationName + "!A1:F" + (customers.size() + 1));
        sheet.setMajorDimension("ROWS");
        List<Object> title = titleForGoogleSheet();
        List<List<Object>> resultList = new ArrayList<>();
        resultList.add(title);
        customers.forEach(
                customer -> {
                    List<Object> jCustomer = parseCustomerForSheetsData(customer);
                    resultList.add(jCustomer);
                }
        );
        sheet.setValues(resultList);
        return sheet;
    }

    public void updateSheet() throws IOException {
        Sheet sheet = prepareSheetData();
        String valueInputOption = "RAW";
        ValueRange requestBody = new ValueRange();
        requestBody.setValues(sheet.getValues());
        requestBody.setRange(sheet.getRange());
        requestBody.setMajorDimension("ROWS");

        Sheets.Spreadsheets.Values.Update request =
                sheetsService.spreadsheets().values().update(spreadsheetId, sheet.getRange(), requestBody);
        request.setValueInputOption(valueInputOption);
        request.execute();
    }

}
