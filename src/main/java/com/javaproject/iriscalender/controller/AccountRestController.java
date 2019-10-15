package com.javaproject.iriscalender.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @Controller + @ResponseBody 의 축약형으로써, return 값을 view resolver 로 매핑하지 않고 그대로 출력해준다.
@RestController
public class AccountRestController {

    @GetMapping("/hello")
    public String hello() {
        return "HelloWorld";
    }
}