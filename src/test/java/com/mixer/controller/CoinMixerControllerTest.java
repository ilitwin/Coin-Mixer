package com.mixer.controller;

import com.mixer.service.CoinMixerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class CoinMixerControllerTest {

    @Mock
    CoinMixerService coinMixerService;

    @InjectMocks
    CoinMixerController coinMixerController;

    @Test
    public void shouldCallCoinMixerService() {
        List<String> addresses = new ArrayList<>();
        coinMixerController.startMixing(addresses);
        verify(coinMixerService, times(1)).startMixing(addresses);
    }

}