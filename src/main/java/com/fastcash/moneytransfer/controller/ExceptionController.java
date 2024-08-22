package com.fastcash.moneytransfer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
@ApiBaseUrlPrefix
public class ExceptionController {

    @GetMapping("/simulate-error")
    public void simulateInternalServerError() {
        // Simulate an internal server error by throwing an exception
        throw new RuntimeException("Internal Server Error occurred");
    }
}