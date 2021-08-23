/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.util.function.Function;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;


/**
 *
 * This configuration class showcases two styles of configuration; Standard @Bean as well as function bean definition (commented)
 * Basically comment one, un-comment the other, build and deploy.
 *
 * @author Oleg Zhurakousky (Spring/VMWARE)
 * @author Mark Sailes (AWS Lambda)
 *
 */
@SpringBootApplication
public class FunuctionConfiguration {

	public static void main(String[] args) {
		FunctionalSpringApplication.run(FunuctionConfiguration.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return new Uppercase();
	}
}

//@SpringBootApplication
//public class FunuctionConfiguration implements ApplicationContextInitializer<GenericApplicationContext> {
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
