package ru.sberbankschool.restaurantcustomers.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @Column(name = "phone_number")
    private long phoneNumber;
    @Column(name = "name")
    private String name;
    @Column(name = "e_mail")
    private String email;
    @Column(name = "address")
    private String address;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Rating> rating = new ArrayList<>();
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Tips> tips = new ArrayList<>();

    public Customer() {
    }

    public Customer(List<Object> client) {
        this.phoneNumber = Long.parseLong(String.valueOf(client.get(0)));
        this.name = String.valueOf(client.get(1));
        this.email = String.valueOf(client.get(2));
        this.address = String.valueOf(client.get(3));
    }

    @Override
    public String toString() {
        return "Имя: " + name +
                "\nТелефонный номер: " + phoneNumber +
                "\nЭлектронная почта: " + email +
                "\nАдрес: " + address;
    }
}

