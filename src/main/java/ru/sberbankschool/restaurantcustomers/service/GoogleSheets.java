package ru.sberbankschool.restaurantcustomers.service;

import ru.sberbankschool.restaurantcustomers.entity.Customer;

import java.util.List;

public interface GoogleSheets {
    List<Customer> getValues();
    Customer findCustomerByPhoneNumber(long phoneNumber);
    }
