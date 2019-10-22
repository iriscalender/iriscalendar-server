package com.javaproject.iriscalender.controller;

import com.javaproject.iriscalender.exception.AlreadyExistException;
import com.javaproject.iriscalender.model.entity.User;
import com.javaproject.iriscalender.model.request.LoginParam;
import com.javaproject.iriscalender.model.response.Token;
import com.javaproject.iriscalender.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;


// @Controller + @ResponseBody 의 축약형으로써, return 값을 view resolver 로 매핑하지 않고 그대로 출력해준다.
@RestController
@RequestMapping("/auth")
public class AccountRestController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody LoginParam param) {
        String userId = param.getId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            throw new AlreadyExistException("Account Already Exist");
        } else {
            User newUser = User.builder()
                    .userId(param.getId())
                    .userPassword(param.getPassword())
                    .build();

            userRepository.save(newUser);
            Token token = new Token(newUser);
            return ok(token.getTokenResponse());
        }
    }

//    @PostMapping("/signin")
//    public ResponseEntity signIn(@RequestBody User userRequest) {
//        try {
//            String username = userRequest.getUserID();}
//        }
//    }
}
