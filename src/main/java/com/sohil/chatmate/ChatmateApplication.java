package com.sohil.chatmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.reflect.Method;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ChatmateApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(ChatmateApplication.class, args);
	}

}
