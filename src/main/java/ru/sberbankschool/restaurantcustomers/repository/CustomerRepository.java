package ru.sberbankschool.restaurantcustomers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbankschool.restaurantcustomers.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findById(long id);

    Customer findByEmail(String email);
}
