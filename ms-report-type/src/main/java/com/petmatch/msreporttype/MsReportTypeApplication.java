package com.petmatch.msreporttype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsReportTypeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReportTypeApplication.class, args);
    }
}
