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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Oleg Zhurakousky (Spring/VMWARE)
 * @author Mark Sailes (AWS Lambda)
 *
 */
@SpringBootApplication
public class FunctionConfiguration {


	public static void main(String[] args) {
		SpringApplication.run(FunctionConfiguration.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return value -> {
			System.out.println("Uppercasing " + value);
			return value.toUpperCase();
		};
	}

	@Bean
	public Function<String, String> reverse() {
		return value -> {
			System.out.println("Reversing " + value);
			return new StringBuilder(value).reverse().toString();
		};
	}

	@Bean
	public Function<Person, Person> uppercasePerson() {
		return inPerson -> {
			Person person = new Person();
			person.setId(inPerson.getId());
			person.setName(inPerson.getName().toUpperCase());
			return person;
		};
	}

}

class Person {
	private int id;

	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}