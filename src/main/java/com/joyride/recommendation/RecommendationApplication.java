package com.joyride.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class RecommendationApplication {

    public static void main(String[] args) {
        System.setProperty("javax.net.debug", "ssl,handshake");
        SpringApplication.run(RecommendationApplication.class, args);
    }

}
