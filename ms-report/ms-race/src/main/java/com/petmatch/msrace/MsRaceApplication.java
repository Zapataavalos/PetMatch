package com.petmatch.msrace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsRaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsRaceApplication.class, args);
    }
}
