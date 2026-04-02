package com.com2us.mobility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MobilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobilityApplication.class, args);
    }

}
