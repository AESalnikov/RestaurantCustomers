package ru.sberbankschool.restaurantcustomers.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sberbankschool.restaurantcustomers.dao.CustomerDao;
import ru.sberbankschool.restaurantcustomers.service.GoogleSheetsService;

@EnableScheduling
@Component
@Slf4j
public class ScheduledTask {

    private final CustomerDao dao;
    private final GoogleSheetsService googleSheetsService;

    public ScheduledTask(CustomerDao dao, GoogleSheetsService googleSheetsService) {
        this.dao = dao;
        this.googleSheetsService = googleSheetsService;
    }

//    @Scheduled(fixedRate = 60000)
    @Scheduled(cron = "0 0 10 * * *")
    public void updateDataBase() {
        dao.saveAllCustomersFromGoogleSheet(googleSheetsService.getValues());
        log.info("База данных обновлена!");
    }
}
