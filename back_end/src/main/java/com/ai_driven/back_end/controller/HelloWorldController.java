package com.ai_driven.back_end.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai_driven.back_end.dto.HelloWorldResponse;

@RestController
@RequestMapping("/api/demo")
public class HelloWorldController {

	@GetMapping("/helloworld")
	public HelloWorldResponse helloWorld() {
		return new HelloWorldResponse("Hello World!");
	}

}
