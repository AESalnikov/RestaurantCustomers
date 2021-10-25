package ru.sberbankschool.restaurantcustomers.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerDao {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerDao(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findCustomerByPhoneNumber(long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> findCustomerByFullName(String lastName, String firstName, String secondName) {
        return customerRepository.findByLastNameAndFirstNameAndSecondName(lastName, firstName, secondName);
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public void removeCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void saveAllCustomersFromGoogleSheet(List<Customer> customers) {
        customerRepository.saveAll(customers);
    }

    public boolean isExist(long phoneNumber) {
        Customer customer = findCustomerByPhoneNumber(phoneNumber);
        return customer != null;
    }
}

