package com.techieAshutosh.service;

import com.techieAshutosh.dto.LoginDto;
import com.techieAshutosh.dto.UserDto;
import com.techieAshutosh.model.User;
import com.techieAshutosh.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Mono<User> addUser(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUsername())
                .flatMap(user -> Mono.error(new IllegalArgumentException("Username is already taken")))
                .switchIfEmpty(userRepository.findByEmail(userDto.getEmail())
                        .flatMap(user -> Mono.error(new IllegalArgumentException("Email is already registered"))))
                .then(Mono.defer(() -> {
                    User user = new User();
                    user.setFirstName(userDto.getFirstName());
                    user.setLastName(userDto.getLastName());
                    user.setUsername(userDto.getUsername());
                    user.setEmail(userDto.getEmail());
                    user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt(10)));
                    user.setUserRole(userDto.getUserRole());
                    return userRepository.save(user);
                }));
    }

    public Mono<String> verifyLogin(LoginDto loginDto) {
        return userRepository.findByUsername(loginDto.getUsername())
                .flatMap(user -> {
                    if (BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
                        return Mono.just(jwtService.generateToken(user));
                    }
                    return Mono.error(new IllegalArgumentException("Invalid credentials"));
                });
    }
}
