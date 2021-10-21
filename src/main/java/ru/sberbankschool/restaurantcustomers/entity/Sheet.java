package ru.sberbankschool.restaurantcustomers.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Sheet {
    String range;
    String majorDimension;
    List<List<String>> values;
}
