package com.example.demo;

import java.util.function.Function;

public class Uppercase implements Function<String, String> {

	@Override
	public String apply(String input) {
		System.out.println("Uppercasing " + input);
		return input.toUpperCase();
	}

}
