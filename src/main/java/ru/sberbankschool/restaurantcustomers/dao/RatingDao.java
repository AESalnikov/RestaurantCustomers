package ru.sberbankschool.restaurantcustomers.dao;

import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Rating;
import ru.sberbankschool.restaurantcustomers.repository.RatingRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingDao {
    private final RatingRepository ratingRepository;

    public RatingDao(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public List<Integer> getCustomersMarkByPhoneNumber(Customer customer) {
        return ratingRepository.findByCustomerId(customer.getPhoneNumber())
                .stream()
                .map(Rating::getMark)
                .collect(Collectors.toList());
    }

    public void saveCustomerMark(Rating rating) {
        ratingRepository.save(rating);
    }
}
