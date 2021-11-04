package ru.sberbankschool.restaurantcustomers.dao;

import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerDao {

    private final CustomerRepository customerRepository;

    public CustomerDao(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomerByPhoneNumber(long id) {
        return customerRepository.findByPhoneNumber(id);
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void saveAllCustomersFromGoogleSheet(List<Customer> customers) {
        customerRepository.saveAll(customers);
    }
}

