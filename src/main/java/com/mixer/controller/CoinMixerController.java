package com.mixer.controller;

import com.mixer.service.CoinMixerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
public class CoinMixerController {

    @Autowired
    CoinMixerService coinMixerService;

    @PostMapping()
    public String startMixing(@RequestBody List<String> addresses){
        return coinMixerService.startMixing(addresses);
    }

    @GetMapping("watch")
    public void watch(){
        coinMixerService.watch();
    }

}
