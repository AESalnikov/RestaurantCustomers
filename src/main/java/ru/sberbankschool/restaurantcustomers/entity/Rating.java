package ru.sberbankschool.restaurantcustomers.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "mark")
    private int mark;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
