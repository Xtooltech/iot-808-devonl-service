package com.xtool.devonl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Iot808ServiceDevonlApplication {

	public static void main(String[] args) {
		SpringApplication.run(Iot808ServiceDevonlApplication.class, args);
	}

}

