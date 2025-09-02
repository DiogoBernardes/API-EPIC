package com.epic.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.epic.auth", "com.epic.shared"})
public class AuthApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(AuthApplication.class, args);
    }
}
