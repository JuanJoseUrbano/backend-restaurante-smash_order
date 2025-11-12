package com.restaurant.SmashOrder.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/new")
public class NewController {

    @GetMapping
    public String hello() {
        return "Hello from New Controller!";
    }
}
