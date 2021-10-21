package ru.sberbankschool.restaurantcustomers.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.dao.CustomerDao;
import ru.sberbankschool.restaurantcustomers.entity.Customer;

import java.util.List;

@Getter
@Setter
@Service
public class DbService {
    private CustomerDao customerDao;

    public DbService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void saveCustomerFromGoogleSheet(Customer customer) {
        customerDao.saveCustomer(customer);
    }

    public void saveAllCustomersFromGoogleSheet(List<Customer> customers) {
        customers.forEach(customer -> customerDao.saveCustomer(customer));
    }

    public Customer getCustomerFromDataBase(long phoneNumber) {
        return customerDao.findCustomerByPhoneNumber(phoneNumber);
    }

    public List<Customer> getCustomerByNameFromDataBase(String lastName, String firstName, String secondName) {
        return customerDao.findCustomerByName(lastName, firstName, secondName);
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAllCustomers();
    }
}
