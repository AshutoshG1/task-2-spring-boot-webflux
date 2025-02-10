package com.techieAshutosh.controller;

import com.techieAshutosh.dto.LoginDto;
import com.techieAshutosh.dto.UserDto;
import com.techieAshutosh.model.User;
import com.techieAshutosh.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public Mono<User> addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);

    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginDto loginDto) {
        return userService.verifyLogin(loginDto);

    }
}