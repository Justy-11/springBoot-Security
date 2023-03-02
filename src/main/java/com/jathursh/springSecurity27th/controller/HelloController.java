package com.jathursh.springSecurity27th.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){

        //TODO:only allow - only when logged in
        return "Hello";
    }
}
