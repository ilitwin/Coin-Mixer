package com.mixer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class Address {

    private String balance;
    private List<Transaction> transactions;

}
