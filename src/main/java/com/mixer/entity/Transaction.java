package com.mixer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date timestamp;
    private String toAddress;
    private String fromAddress;
    private String amount;

    public Transaction(String toAddress, String fromAddress, String amount) {
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.amount = amount;
    }

}
