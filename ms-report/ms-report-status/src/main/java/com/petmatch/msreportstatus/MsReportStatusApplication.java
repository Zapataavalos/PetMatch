package com.petmatch.msreportstatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsReportStatusApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReportStatusApplication.class, args);
    }
}
