package com.petmatch.msanimaltype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsAnimalTypeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAnimalTypeApplication.class, args);
    }
}
