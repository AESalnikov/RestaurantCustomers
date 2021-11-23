package ru.sberbankschool.restaurantcustomers.service;

import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Rating;
import ru.sberbankschool.restaurantcustomers.entity.Tips;

import java.util.List;

public interface DatabaseService {

    Customer getCustomerByPhoneNumber(long id);
    Customer getCustomerByEmail(String email);
    List<Customer> getAllCustomers();
    void saveCustomer(Customer customer);
    void saveAllCustomersFromGoogleSheet(List<Customer> customers);
    List<Integer> getMarks(Customer customer);
    void saveMark(Rating rating);
    List<String> getTips(Customer customer);
    void saveTips(Tips tips);
}
