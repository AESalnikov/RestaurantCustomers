package ru.sberbankschool.restaurantcustomers.service;

import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.dao.CustomerDao;
import ru.sberbankschool.restaurantcustomers.dao.RatingDao;
import ru.sberbankschool.restaurantcustomers.dao.TipsDao;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Rating;
import ru.sberbankschool.restaurantcustomers.entity.Tips;

import java.util.List;

@Service
public class DbService {
    CustomerDao customerDao;
    RatingDao ratingDao;
    TipsDao tipsDao;

    public DbService(CustomerDao customerDao, RatingDao ratingDao, TipsDao tipsDao) {
        this.customerDao = customerDao;
        this.ratingDao = ratingDao;
        this.tipsDao = tipsDao;
    }

    public Customer getCustomerByPhoneNumber(long id) {
        return customerDao.findCustomerByPhoneNumber(id);
    }

    public Customer getCustomerByEmail(String email) {
        return customerDao.findCustomerByEmail(email);
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAllCustomers();
    }

    public void saveCustomer(Customer customer) {
        customerDao.saveCustomer(customer);
    }

    public void saveAllCustomersFromGoogleSheet(List<Customer> customers) {
        customerDao.saveAllCustomersFromGoogleSheet(customers);
    }

    public List<Integer> getMarks(Customer customer) {
        return ratingDao.getCustomersMarkByPhoneNumber(customer);
    }

    public void saveMark(Rating rating) {
        ratingDao.saveCustomerMark(rating);
    }

    public List<String> getTips(Customer customer) {
        return tipsDao.getCustomersTipsByPhoneNumber(customer);
    }

    public void saveTips(Tips tips) {
        tipsDao.saveCustomerTips(tips);
    }
}
