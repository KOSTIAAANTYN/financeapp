package com.financeprojectboard.app;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.repositories.UserRepository;
import com.financeprojectboard.app.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}
