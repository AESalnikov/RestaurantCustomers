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
    @Value("${sheets.range}")
    private String range;
    @Value("${sheets.key}")
    private String key;
}

