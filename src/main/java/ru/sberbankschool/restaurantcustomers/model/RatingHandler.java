package ru.sberbankschool.restaurantcustomers.model;

import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.service.DbService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RatingHandler {

    private DbService dbService;

    public RatingHandler(DbService dbService) {
        this.dbService = dbService;
    }
    public double getTips(Customer customer) {
        List<String> tips = dbService.getTips(customer);
        int trueTips = tips.stream().filter(t -> Objects.equals(Boolean.valueOf(t), true)).collect(Collectors.toList()).size();
        double result = trueTips / (double) tips.size();
        return ((double) Math.round(result * 10) / 10) * 100;
    }

    public double getRating(Customer customer) {
        List<Integer> marks = dbService.getMarks(customer);
        int sum = 0;
        for (Integer mark: marks)
            sum += mark;
        double result = sum / (double) marks.size();
        return (double) Math.round(result * 10) / 10;
    }
}
