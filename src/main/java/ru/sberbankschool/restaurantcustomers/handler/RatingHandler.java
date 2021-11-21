package ru.sberbankschool.restaurantcustomers.handler;

import org.springframework.stereotype.Component;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DatabaseService;

import java.util.List;
import java.util.Objects;

@Component
public class RatingHandler {

    private final DatabaseService dbService;

    public RatingHandler(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public double getTips(Customer customer) {
        List<String> tips = dbService.getTips(customer);
        int trueTips = (int) tips.stream().filter(t -> Objects.equals(Boolean.valueOf(t), true)).count();
        double result = (double) trueTips / tips.size() * 100;
        return (double) Math.round(result * 10) / 10;
    }

    public double getRating(Customer customer) {
        List<Integer> marks = dbService.getMarks(customer);
        double sum = 0;
        for (Integer mark : marks)
            sum += mark;
        double result = sum / marks.size();
        return (double) Math.round(result * 10) / 10;
    }
}
