package ru.sberbankschool.restaurantcustomers.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tips")
public class Tips {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "tips")
    private String tips;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
