package com.example.demo;

import java.util.function.Function;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;


@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		FunctionalSpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return new Uppercase();
	}
}

//@SpringBootApplication
//public class DemoApplication implements ApplicationContextInitializer<GenericApplicationContext> {
//
//	public static void main(String[] args) {
//		FunctionalSpringApplication.run(DemoApplication.class, args);
//	}
//
//	@Override
//	public void initialize(GenericApplicationContext context) {
//		context.registerBean("uppercase", FunctionRegistration.class,
//				() -> new FunctionRegistration<>(new Uppercase()).type(FunctionType.from(String.class).to(String.class)));
//	}
//}
