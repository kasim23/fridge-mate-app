package com.fridgemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication  // component scan + auto-config + configuration root
@EnableScheduling       // activates @Scheduled methods (our expiry check cron job)
public class FridgeMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FridgeMateApplication.class, args);
    }
}
