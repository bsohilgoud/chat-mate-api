package com.sohil.chatmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ChatmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatmateApplication.class, args);
	}

}
