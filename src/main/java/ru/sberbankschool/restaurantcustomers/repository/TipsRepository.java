package ru.sberbankschool.restaurantcustomers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sberbankschool.restaurantcustomers.entity.Tips;

import java.util.List;

public interface TipsRepository extends JpaRepository<Tips, Integer> {
    @Query("SELECT t from Tips t WHERE t.customer.phoneNumber = :#{#phoneNumber}  ")
    List<Tips> findByCustomerId(Long phoneNumber);
}
