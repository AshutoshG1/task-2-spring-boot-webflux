package com.techieAshutosh.controllerTest;


import com.techieAshutosh.controller.UserController;
import com.techieAshutosh.dto.LoginDto;
import com.techieAshutosh.dto.UserDto;
import com.techieAshutosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(userController).build();
    }

    @Test
    void addUser_Success() {
        UserDto userDto = new UserDto("John", "Doe", "john_doe", "john@example.com", "password", "USER");

        when(userService.addUser(any(UserDto.class))).thenReturn(Mono.just(new com.techieAshutosh.model.User()));

        webTestClient.post()
                .uri("/api/v1/users/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Registration successful");
    }

    @Test
    void addUser_Failure() {
        UserDto userDto = new UserDto("John", "Doe", "john_doe", "john@example.com", "password", "USER");

        when(userService.addUser(any(UserDto.class))).thenReturn(Mono.error(new IllegalArgumentException("Username is already taken")));

        webTestClient.post()
                .uri("/api/v1/users/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Username is already taken");
    }

    @Test
    void login_Success() {
        LoginDto loginDto = new LoginDto("john_doe", "password");
        when(userService.verifyLogin(any(LoginDto.class))).thenReturn(Mono.just("mock_token"));

        webTestClient.post()
                .uri("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("mock_token");
    }

    @Test
    void login_Failure() {
        LoginDto loginDto = new LoginDto("john_doe", "wrong_password");
        when(userService.verifyLogin(any(LoginDto.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid credentials")));

        webTestClient.post()
                .uri("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.token").isEqualTo("Invalid credentials");
    }
}
