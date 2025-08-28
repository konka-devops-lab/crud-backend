package com.example.crudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CrudAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudAppApplication.class, args);
    }
}