package ru.sberbankschool.restaurantcustomers.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbankschool.restaurantcustomers.service.DbService;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;

@EnableScheduling
@Component
@Slf4j
public class ScheduledTask {

    private final DbService dbService;
    private final GoogleSheetsService googleSheetsService;

    public ScheduledTask(DbService dbService, GoogleSheetsService googleSheetsService) {
        this.dbService = dbService;
        this.googleSheetsService = googleSheetsService;
    }

    @Transactional
    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0 0 10 * * *")
    public void reportCurrentTime() {
        dbService.saveAllCustomersFromGoogleSheet(googleSheetsService.getValues());
        log.info("База данных обновлена!");
    }
}
