package com.techieAshutosh.config;

import com.techieAshutosh.repository.UserRepository;
import com.techieAshutosh.service.JWTService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JWTRequestFilter implements WebFilter {
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JWTRequestFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            return jwtService.getUserName(token)
                    .flatMap(userRepository::findByUsername)
                    .map(user -> {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null, Collections.singleton(new SimpleGrantedAuthority(user.getUserRole()))
                        );
                        return new SecurityContextImpl(authentication);
                    })
                    .flatMap(securityContext ->
                            chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                    );
        }

        return chain.filter(exchange);
    }
}
