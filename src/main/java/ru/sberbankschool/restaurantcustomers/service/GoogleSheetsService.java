package ru.sberbankschool.restaurantcustomers.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sberbankschool.restaurantcustomers.config.SheetsConfig;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Sheet;
import ru.sberbankschool.restaurantcustomers.handler.RatingHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleSheetsService implements GoogleSheets {
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final String APPLICATION_NAME;
    private final String SPREADSHEET_ID;
    private final String RANGE;
    private final String KEY;
    private final String CREDENTIALS_FILE_PATH;
    private final String TOKENS_DIRECTORY_PATH;
    private final DbService DB_SERVICE;
    private final Sheets SHEETS_SERVICE;

    public GoogleSheetsService(SheetsConfig sheetsConfig, DbService dbService) throws GeneralSecurityException, IOException {
        this.APPLICATION_NAME = sheetsConfig.getApplicationName();
        this.SPREADSHEET_ID = sheetsConfig.getSpreadsheetId();
        this.RANGE = sheetsConfig.getRange();
        this.KEY = sheetsConfig.getKey();
        this.CREDENTIALS_FILE_PATH = sheetsConfig.getCredentials();
        this.TOKENS_DIRECTORY_PATH = sheetsConfig.getTokens();
        this.SHEETS_SERVICE = createSheetsService(APPLICATION_NAME);
        this.DB_SERVICE = dbService;
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        InputStream in = GoogleSheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(5000).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Sheets createSheetsService(String applicationName) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(httpTransport, jsonFactory, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(applicationName)
                .build();
    }

    private String buildUrl() {
        return new StringBuilder(
                "https://sheets.googleapis.com/v4/spreadsheets/" + SPREADSHEET_ID +
                        "/values/" + APPLICATION_NAME + RANGE +
                        "?key=" + KEY
        ).toString();
    }

    @Override
    public List<Customer> getValues() {
        RestTemplate rest = new RestTemplate();
        String sheetLink = buildUrl();
        ResponseEntity<Sheet> responseEntity = rest.getForEntity(sheetLink, Sheet.class);
        List<List<Object>> values = responseEntity.getBody().getValues();
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

    private Sheet prepareSheetData() {
        List<Customer> customers = DB_SERVICE.getAllCustomers();
        Sheet sheet = new Sheet();
        sheet.setRange(APPLICATION_NAME + "!A1:F" + (customers.size() + 1));
        sheet.setMajorDimension("ROWS");
        List<Object> title = new ArrayList<>();
        title.add("Номер телефона");
        title.add("Имя");
        title.add("Электронная почта");
        title.add("Адрес");
        title.add("Чаевые");
        title.add("Рейтинг");
        List<List<Object>> resultList = new ArrayList<>();
        resultList.add(title);
        customers.forEach(
                customer -> {
                    List<Object> jCustomer = new ArrayList<>();
                    jCustomer.add(customer.getPhoneNumber());
                    jCustomer.add(customer.getName());
                    jCustomer.add(customer.getEmail());
                    jCustomer.add(customer.getAddress());
                    jCustomer.add(String.valueOf(new RatingHandler(DB_SERVICE).getTips(customer)));
                    jCustomer.add(String.valueOf(new RatingHandler(DB_SERVICE).getRating(customer)));
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
                SHEETS_SERVICE.spreadsheets().values().update(SPREADSHEET_ID, sheet.getRange(), requestBody);
        request.setValueInputOption(valueInputOption);
        request.execute();
    }

}
