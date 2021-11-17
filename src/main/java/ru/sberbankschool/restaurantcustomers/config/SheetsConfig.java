package ru.sberbankschool.restaurantcustomers.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SheetsConfig {
    @Value("${sheets.name}")
    private String applicationName;
    @Value("${sheets.id}")
    private String spreadsheetId;
    @Value("${sheets.credentials}")
    private String credentials;
    @Value("${sheets.admin}")
    private String serviceAdmin;
}

