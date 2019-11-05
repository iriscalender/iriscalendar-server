package com.javaproject.iriscalender.controller;

import com.javaproject.iriscalender.exception.AlreadyExistException;
import com.javaproject.iriscalender.exception.WrongException;
import com.javaproject.iriscalender.model.entity.User;
import com.javaproject.iriscalender.model.request.SignInModel;
import com.javaproject.iriscalender.model.request.SignUpModel;

import com.javaproject.iriscalender.model.response.TokenResponse;
import com.javaproject.iriscalender.service.AuthService;
import com.javaproject.iriscalender.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;


// @Controller + @ResponseBody 의 축약형으로써, return 값을 view resolver 로 매핑하지 않고 그대로 출력해준다.
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    TokenService tokenService;

    @PostMapping("/signup")
    public TokenResponse signUp(@RequestBody SignUpModel signUpModel) {
        Optional<User> user = authService.getUserById(signUpModel.getId());
        if (user.isPresent()) {
            throw new AlreadyExistException("Account Already Exist");
        } else {
            User newUser = User.builder()
                    .userId(signUpModel.getId())
                    .userPassword(signUpModel.getPassword())
                    .build();

            authService.saveUser(newUser);
            return new TokenResponse(tokenService.createToken(newUser.getUserId()));
        }
    }

    @PostMapping("/signin")
    public TokenResponse signIn(@RequestBody SignInModel signInModel) {
        Optional<User> user = authService.getUserById(signInModel.getId());
        if (user.isPresent()) {
            if (user.get().getUserPassword().equals(signInModel.getPassword())) {
                return new TokenResponse(tokenService.createToken(user.get().getUserId()));
            }
        }

        throw new WrongException("Bad param");
    }
}
