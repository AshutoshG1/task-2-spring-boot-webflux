package com.techieAshutosh.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.techieAshutosh.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String algorithmKey; // Secret key

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiry.duration}")
    private int expiryTime;

    private Algorithm algorithm;
    private static final String USER_NAME = "username";

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withClaim(USER_NAME, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryTime))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public Mono<String> getUserName(String token) {
        return Mono.fromCallable(() -> {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);
            return decodedJWT.getClaim(USER_NAME).asString();
        });
    }
}