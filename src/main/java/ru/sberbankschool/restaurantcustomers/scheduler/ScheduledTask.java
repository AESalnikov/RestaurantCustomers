package ru.sberbankschool.restaurantcustomers.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sberbankschool.restaurantcustomers.service.DatabaseServiceImpl;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;

import java.io.IOException;

@EnableScheduling
@Component
@Slf4j
public class ScheduledTask {

    private final DatabaseServiceImpl dbService;
    private final GoogleSheetsService googleSheetsService;

    public ScheduledTask(DatabaseServiceImpl dbService, GoogleSheetsService googleSheetsService) {
        this.dbService = dbService;
        this.googleSheetsService = googleSheetsService;
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void updateData() throws IOException {
        dbService.saveAllCustomersFromGoogleSheet(googleSheetsService.getValues());
        log.info("База данных обновлена!");
        googleSheetsService.updateSheet();
        log.info("Гугл таблица обновлена!");
    }
}
