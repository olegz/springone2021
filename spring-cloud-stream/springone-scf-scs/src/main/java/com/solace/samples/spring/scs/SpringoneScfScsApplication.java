package com.solace.samples.spring.scs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
public class SpringoneScfScsApplication {
	
	private static final Logger log = LoggerFactory.getLogger(SpringoneScfScsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringoneScfScsApplication.class, args);
	}
	
	@Bean 
	public Function<String, String> uppercase(){
		return v -> {
			log.info("Uppercasing: " + v);
			return v.toUpperCase();
		};
	}

	
	
	
	
	
	
	/*
	 * Dynamic Topic Publish OPTION 1: Using StreamBridge; works with any binder
	 * 
	 * StreamBridge caches a channel within Spring for each destination.
	 * 
	 * The number of channels cached is configurable via
	 * `spring.cloud.stream.dynamic-destination-cache-size`
	 */
//	@Bean
//	public Consumer<String> functionUsingStreamBridge(StreamBridge streamBridge) {
//		return input -> {
//			String topic = getMyTopicUsingLogic(input);
//			log.info("Processing message: " + input);
//			String payload = input.concat(" Processed by functionUsingStreamBridge");
//			streamBridge.send(topic, payload);
//		};
//	}
	
	
	/*
	 * Dynamic Topic Publish OPTION 2: Using a header
	 * 
	 * Here we are using the `scst_targetDestination` header
	 * 
	 * Note that the BinderHeaders.TARGET_DESTINATION header is essentially telling the
	 * binder to override the default destination specified on a binding and if the
	 * header is NOT set then the message would be sent to the default destination.
	 */
//	@Bean
//	public Function<String, Message<String>> functionUsingTargetDestHeader() {
//		return input -> {
//			String topic = getMyTopicUsingLogic(input);
//			log.info("Processing message: " + input);
//			String payload = input.concat(" Processed by functionUsingTargetDestHeader");
//			return MessageBuilder.withPayload(payload).setHeader(BinderHeaders.TARGET_DESTINATION, topic).build();
//		};
//	}


	private static AtomicInteger counter = new AtomicInteger(0);

	private String getMyTopicUsingLogic(String input) {
		// TODO Use whatever logic you'd like! Order Number? Account Number? Location? Event Type? Severity? Status?  
		return "hello/spring/one/" + counter.getAndIncrement();
	}
}
