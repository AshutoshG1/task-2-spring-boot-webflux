package com.techieAshutosh.controller;

import com.techieAshutosh.dto.LoginDto;
import com.techieAshutosh.dto.UserDto;
import com.techieAshutosh.dto.TokenResponse;
import com.techieAshutosh.service.UserService;
import org.springframework.http.ResponseEntity;
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
    public Mono<ResponseEntity<String>> addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto)
                .map(user -> ResponseEntity.ok("Registration successful"))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody LoginDto loginDto) {
        return userService.verifyLogin(loginDto)
                .map(token -> ResponseEntity.ok(new TokenResponse(token)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).body(new TokenResponse("Invalid credentials"))));
    }
}