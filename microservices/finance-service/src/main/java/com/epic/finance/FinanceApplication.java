package com.epic.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.epic"})
@EnableFeignClients(basePackages = "com.epic.finance.client")
public class FinanceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(FinanceApplication.class, args);
    }
}
