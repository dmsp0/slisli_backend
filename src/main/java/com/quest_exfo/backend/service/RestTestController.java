package com.quest_exfo.backend.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestTestController {
//    Postman 연결 확인용!

    @GetMapping("/getTest")
    public String getTest(){
        return "Hello World!";
    }

    @PostMapping("/postTest")
    public String postTest() {
        return "Hello World!";
    }
}