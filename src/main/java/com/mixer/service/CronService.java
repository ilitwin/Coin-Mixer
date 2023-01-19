package com.mixer.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
@Service
@EnableScheduling
public class CronService {

    @Value(value = "${internal.services.url}")
    private String internalServicesUrl;

    /**
     * This cron makes a request every 60 seconds to check for any transactions that need to be executed
     */

    @SneakyThrows
    @Scheduled(cron = "${cron-watch-interval}")
    public void runTheApp() {

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(internalServicesUrl + "/watch")).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Listening...");
    }

}
