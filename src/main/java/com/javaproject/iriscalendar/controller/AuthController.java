package com.javaproject.iriscalendar.controller;

import com.javaproject.iriscalendar.exception.AlreadyExistException;
import com.javaproject.iriscalendar.exception.NoMatchException;
import com.javaproject.iriscalendar.model.entity.User;
import com.javaproject.iriscalendar.model.request.SignInModel;
import com.javaproject.iriscalendar.model.request.SignUpModel;

import com.javaproject.iriscalendar.model.response.TokenResponse;
import com.javaproject.iriscalendar.service.AuthService;
import com.javaproject.iriscalendar.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


// @Controller + @ResponseBody 의 축약형으로써, return 값을 view resolver 로 매핑하지 않고 그대로 출력해준다.
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    TokenService tokenService;

    @PostMapping("/signup")
    public TokenResponse signUp(@RequestBody @Valid SignUpModel signUpModel) {
         Optional<User> user = authService.getUserById(signUpModel.getId());
        if (user.isPresent()) {
            throw new AlreadyExistException("Account Already Exist");
        } else {
            User newUser = User.builder()
                    .id(signUpModel.getId())
                    .password(signUpModel.getPassword())
                    .build();

            authService.saveUser(newUser);
            return new TokenResponse(tokenService.createToken(newUser.getUserId()));
        }
    }

    @PostMapping("/signin")
    public TokenResponse signIn(@RequestBody @Valid SignInModel signInModel) {
        Optional<User> user = authService.getUserById(signInModel.getId());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(signInModel.getPassword())) {
                return new TokenResponse(tokenService.createToken(user.get().getId()));
            }
        }

        throw new NoMatchException("no users match");
    }
}
