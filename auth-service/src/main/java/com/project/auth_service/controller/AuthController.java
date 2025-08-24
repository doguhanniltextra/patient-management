package com.project.auth_service.controller;


import com.project.auth_service.dto.LoginRequestDto;
import com.project.auth_service.dto.LoginResponseDto;
import com.project.auth_service.dto.RegisterRequestDto;
import com.project.auth_service.dto.RegisterResponseDto;
import com.project.auth_service.entity.User;
import com.project.auth_service.repository.UserRepository;
import com.project.auth_service.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        if(userRepository.existsByName(registerRequestDto.getName())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setName(registerRequestDto.getName());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setEmail(registerRequestDto.getEmail());
        user.setRegisterDate(registerRequestDto.getRegisterDate());

        userRepository.save(user);

        String token = jwtService.generateToken(user.getName());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new RegisterResponseDto("User registered successfully", token));
    }

    @PostMapping(path = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        User user = userRepository.findByName(loginRequestDto.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }


        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getName());

        return ResponseEntity.ok(new LoginResponseDto("Login successful", token));
    }

}
