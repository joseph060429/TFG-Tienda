package com.proyecto.tienda.backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
// @PropertySource(value = {"classpath:application.properties"})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
