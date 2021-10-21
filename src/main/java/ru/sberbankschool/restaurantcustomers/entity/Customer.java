package ru.sberbankschool.restaurantcustomers.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @Column(name = "phone_number")
    private long phoneNumber;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "social_status")
    private String status;
    @Column(name = "e_mail")
    private String email;
    @Column(name = "address")
    private String address;

    public Customer() {}

    public Customer(List<String> client) {
        this.phoneNumber = Long.valueOf(client.get(0));
        this.lastName = client.get(1);
        this.firstName = client.get(2);
        this.secondName = client.get(3);
        this.status = client.get(4);
        this.email = client.get(5);
        this.address = client.get(6);
    }

    @Override
    public String toString() {
        return "ФИО: " + lastName + " " + firstName + " " + secondName +
                "\nСоциальный статус: " + status +
                "\nТелефонный номер: " + phoneNumber +
                "\nЭлектронная почта: " + email +
                "\nАдрес: " + address;
    }
}

