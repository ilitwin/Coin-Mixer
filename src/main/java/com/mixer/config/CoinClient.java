package com.mixer.config;

import com.mixer.entity.Address;
import com.mixer.entity.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "coin", url = "${external.services.coin-url}")
public interface CoinClient {

    @RequestMapping(method = RequestMethod.GET, value = "addresses/{address}")
    Address getAddressData(@PathVariable("address") String address);

    @RequestMapping(method = RequestMethod.POST, value = "transactions")
    void sendTransactionData(Transaction transaction);

}
