package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.app"})
public class ShopNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopNestApplication.class, args);
	}

}
