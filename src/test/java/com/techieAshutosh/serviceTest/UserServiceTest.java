package com.techieAshutosh.serviceTest;

import com.techieAshutosh.dto.LoginDto;
import com.techieAshutosh.dto.UserDto;
import com.techieAshutosh.model.User;
import com.techieAshutosh.repository.UserRepository;
import com.techieAshutosh.service.JWTService;
import com.techieAshutosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCrypt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUser_Success() {
        UserDto userDto = new UserDto("John", "Doe", "john_doe", "john@example.com", "password", "USER");
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Mono.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addUser(userDto))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addUser_Failure_UsernameTaken() {
        UserDto userDto = new UserDto("John", "Doe", "john_doe", "john@example.com", "password", "USER");
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Mono.just(new User()));

        StepVerifier.create(userService.addUser(userDto))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Username is already taken"))
                .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyLogin_Success() {
        LoginDto loginDto = new LoginDto("john_doe", "password");
        User user = new User();
        user.setUsername(loginDto.getUsername());
        user.setPassword(BCrypt.hashpw(loginDto.getPassword(), BCrypt.gensalt()));

        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Mono.just(user));
        when(jwtService.generateToken(user)).thenReturn("mock_token");

        StepVerifier.create(userService.verifyLogin(loginDto))
                .expectNext("mock_token")
                .verifyComplete();
    }

    @Test
    void verifyLogin_Failure_InvalidCredentials() {
        LoginDto loginDto = new LoginDto("john_doe", "wrong_password");
        User user = new User();
        user.setUsername(loginDto.getUsername());
        user.setPassword(BCrypt.hashpw("correct_password", BCrypt.gensalt()));

        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.verifyLogin(loginDto))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Invalid credentials"))
                .verify();
    }
}
