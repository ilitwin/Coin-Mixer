package com.mixer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AddressMapping {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String mixerAddress;
    private String userAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date lastTransactionTime;

    public AddressMapping(String mixerAddress, String userAddress, Date lastTransactionTime) {
        this.mixerAddress = mixerAddress;
        this.userAddress = userAddress;
        this.lastTransactionTime = lastTransactionTime;
    }

}
