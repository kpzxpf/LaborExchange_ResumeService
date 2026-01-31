package com.vlz.laborexchange_resumeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LaborExchangeResumeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaborExchangeResumeServiceApplication.class, args);
    }

}
