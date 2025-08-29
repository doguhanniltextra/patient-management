package com.project.auth_service.controller;


import com.project.auth_service.dto.LoginRequestDto;
import com.project.auth_service.dto.LoginResponseDto;
import com.project.auth_service.dto.RegisterRequestDto;
import com.project.auth_service.dto.RegisterResponseDto;
import com.project.auth_service.entity.User;
import com.project.auth_service.helper.AuthValidator;
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
    private final AuthValidator authValidator;


    public AuthController(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthValidator authValidator) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authValidator = authValidator;
    }

    @PostMapping(path = "/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        ResponseEntity<String> Username_already_exists = authValidator.checkIfUsernameAlreadyExistsOrNotForRegisterMethod(registerRequestDto, userRepository);
        if (Username_already_exists != null) return Username_already_exists;

        User user = authValidator.registerRequestDtoToUserForRegisterMethod(registerRequestDto, passwordEncoder);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getName());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new RegisterResponseDto("User registered successfully", token));
    }


    @PostMapping(path = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        User user = userRepository.findByName(loginRequestDto.getName())
                .orElse(null);

        ResponseEntity<String> checkUsernameOrPassword = authValidator.checkIfUsernameOrPasswordIsEmptyForLoginMethod(user);
        if (checkUsernameOrPassword != null) return checkUsernameOrPassword;


        ResponseEntity<String> CheckPasswordEncoder = authValidator.CheckIfUsernameOrPasswordIsInvalidForPasswordEncoderForLoginMethod(loginRequestDto, user, passwordEncoder);
        if (CheckPasswordEncoder != null) return CheckPasswordEncoder;

        String token = jwtService.generateToken(user.getName());

        return ResponseEntity.ok(new LoginResponseDto("Login successful", token));
    }
}
