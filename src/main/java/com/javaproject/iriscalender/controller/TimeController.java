package com.javaproject.iriscalender.controller;

import com.javaproject.iriscalender.model.request.SignUpModel;
import com.javaproject.iriscalender.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/time")
public class TimeController {

    @Autowired
    TokenService tokenService;

    @GetMapping("")
    public ResponseEntity getTime(@RequestHeader("Authorization") String auth) {
        String id = tokenService.getIdentity(auth);
    }
    @PostMapping()
    public ResponseEntity signUp(@RequestBody SignUpModel signUpModel)
    @PatchMapping()
    public ResponseEntity signUp(@RequestBody SignUpModel signUpModel)
}
