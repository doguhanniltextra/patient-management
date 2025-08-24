package com.project.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebFluxSecurity
@Configuration
@EnableConfigurationProperties
public class SecurityConfig {

    @Value("${app.secret}")
    private String secret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**", "/api/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    // Disable auto-configuration of UserDetailsService since we're using JWT
    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        // Return a dummy service to prevent auto-configuration
        return username -> Mono.empty();
    }

    @Bean
    public WebFilter jwtAuthenticationFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();

            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Path: " + path);

            // Skip authentication for permitted paths
            if (path.startsWith("/auth/") || path.startsWith("/api/auth/")) {
                System.out.println("Skipping authentication for permitted path");
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println("Authorization header: " + authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("Extracted token: " + token.substring(0, Math.min(20, token.length())) + "...");

                try {
                    System.out.println("Using secret: " + secret);
                    System.out.println("Secret length: " + secret.length());

                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(secret.getBytes())
                            .setAllowedClockSkewSeconds(300) // 5 dakika tolerance
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String subject = claims.getSubject();
                    System.out.println("Token subject: " + subject);
                    System.out.println("Token claims: " + claims);

                    if (subject != null) {
                        // Extract roles if they exist
                        List<String> roles = claims.get("roles", List.class);
                        Collection<SimpleGrantedAuthority> authorities = Collections.emptyList();
                        if (roles != null) {
                            authorities = roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());
                        }

                        // Create authentication
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(subject, token, authorities);

                        System.out.println("Authentication created successfully for user: " + subject);

                        // Set authentication in security context and continue
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                    }
                } catch (Exception e) {
                    System.out.println("JWT parsing error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No valid Authorization header found");
            }

            System.out.println("No authentication set - will result in 401");
            // No valid authentication found - continue without setting auth
            // Spring Security will handle the 401 response due to .authenticated() requirement
            return chain.filter(exchange);
        };
    }
}