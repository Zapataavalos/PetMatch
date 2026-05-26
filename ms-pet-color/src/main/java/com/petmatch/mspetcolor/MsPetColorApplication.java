package com.petmatch.mspetcolor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsPetColorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPetColorApplication.class, args);
    }
}
