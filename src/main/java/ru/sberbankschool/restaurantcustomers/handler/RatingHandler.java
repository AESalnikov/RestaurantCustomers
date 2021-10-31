package ru.sberbankschool.restaurantcustomers.handler;

import org.springframework.stereotype.Component;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RatingHandler {

    private DbService dbService;

    public RatingHandler(DbService dbService) {
        this.dbService = dbService;
    }

    public double getTips(Customer customer) {
        List<String> tips = dbService.getTips(customer);
        int trueTips = tips.stream().filter(t -> Objects.equals(Boolean.valueOf(t), true)).collect(Collectors.toList()).size();
        double result = Double.valueOf(trueTips) / tips.size() * 100;
        return (double) Math.round(result * 10) / 10;
    }

    public double getRating(Customer customer) {
        List<Integer> marks = dbService.getMarks(customer);
        double sum = 0;
        for (Integer mark: marks)
            sum += mark;
        double result = sum / marks.size();
        return (double) Math.round(result * 10) / 10;
    }
}
