package com.carrental.CarRentalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages="com.carrental")
@EnableJpaRepositories(basePackages = "com.carrental.repository")
@EntityScan(basePackages = "com.carrental.entity")
public class CarRentalAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(CarRentalAppApplication.class, args);
	}

}
