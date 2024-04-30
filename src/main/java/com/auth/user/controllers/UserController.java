package com.auth.user.controllers;

import com.auth.user.dtos.LoginRequestDto;
import com.auth.user.dtos.SignupRequestDto;
import com.auth.user.models.Token;
import com.auth.user.models.User;
import com.auth.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public User signUp(@RequestBody SignupRequestDto signupRequestDto){

        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        String name = signupRequestDto.getName();

        return userService.signUp(name, email, password);
    }

    @PostMapping("/login")
    public Token signUp(@RequestBody LoginRequestDto loginRequestDto){

        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        return userService.login(email, password);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("token") String token){

        userService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate/{token}")
    public boolean validateToken(@PathVariable("token") String token){
        return userService.validateToken(token);
    }




}
