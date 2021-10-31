package ru.sberbankschool.restaurantcustomers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sberbankschool.restaurantcustomers.entity.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    @Query("SELECT r from Rating r WHERE r.customer.phoneNumber = :#{#phoneNumber}  ")
    List<Rating> findByCustomerId(Long phoneNumber);
}
