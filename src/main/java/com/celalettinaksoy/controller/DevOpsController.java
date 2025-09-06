package com.celalettinaksoy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

//    http://localhost:8080
@RestController
@RequestMapping
public class DevOpsController {


    //    http://localhost:8080
    @GetMapping
    public String devopsHello() {
        return "Version3 Aksoy Merhaba: " + LocalDateTime.now();
    }


    //    http://localhost:8080/info
    @GetMapping("info")
    public String info() {
        return "Version3 DEVOPS Aksoy INFO: " + LocalDateTime.now();
    }


    //    http://localhost:8080/about
    @GetMapping("about")
    public String about() {
        return "Version3 about: " + LocalDateTime.now();
    }


    //    http://localhost:8080/about
    @GetMapping("entry")
    public String entry() {
        return "Version3 entry: " + LocalDateTime.now();
    }

}
