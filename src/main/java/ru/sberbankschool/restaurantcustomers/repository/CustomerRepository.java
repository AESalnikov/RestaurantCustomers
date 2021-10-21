package ru.sberbankschool.restaurantcustomers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbankschool.restaurantcustomers.entity.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findById(long id);
    List<Customer> findByLastNameAndFirstNameAndSecondName(String lastName, String firstName, String secondName);
}

