package com.midinero.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "Backend funcionando correctamente ✅";
    }

    @GetMapping("/status")
    public String status() {
        return "OK";
    }
}
