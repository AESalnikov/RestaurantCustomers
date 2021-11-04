package ru.sberbankschool.restaurantcustomers.dao;

import org.springframework.stereotype.Service;
import ru.sberbankschool.restaurantcustomers.entity.Customer;
import ru.sberbankschool.restaurantcustomers.entity.Tips;
import ru.sberbankschool.restaurantcustomers.repository.TipsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipsDao {

    TipsRepository tipsRepository;

    public TipsDao(TipsRepository tipsRepository) {
        this.tipsRepository = tipsRepository;
    }

    public List<String> getCustomersTipsByPhoneNumber(Customer customer) {
        return tipsRepository.findByCustomerId(customer.getPhoneNumber())
                .stream()
                .map(Tips::getTips)
                .collect(Collectors.toList());
    }

    public void saveCustomerTips(Tips tips) {
        tipsRepository.save(tips);
    }
}
