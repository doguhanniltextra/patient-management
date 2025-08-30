package com.project.auth_service.controller;


import com.project.auth_service.constants.Endpoints;
import com.project.auth_service.constants.LogMessages;
import com.project.auth_service.dto.LoginRequestDto;
import com.project.auth_service.dto.LoginResponseDto;
import com.project.auth_service.dto.RegisterRequestDto;
import com.project.auth_service.dto.RegisterResponseDto;
import com.project.auth_service.entity.User;
import com.project.auth_service.helper.AuthValidator;
import com.project.auth_service.repository.UserRepository;
import com.project.auth_service.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AUTH_CONTROLLER_REQUEST)
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
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

    @PostMapping(path = Endpoints.REGISTER, produces = Endpoints.PRODUCES)
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {

       log.info(LogMessages.REGISTER_METHOD_TRIGGERED);
        ResponseEntity<String> Username_already_exists = authValidator.checkIfUsernameAlreadyExistsOrNotForRegisterMethod(registerRequestDto, userRepository);
        if (Username_already_exists != null) return Username_already_exists;
        log.info(LogMessages.REGISTER_USERNAME_EXISTS);
        User user = authValidator.registerRequestDtoToUserForRegisterMethod(registerRequestDto, passwordEncoder);

        userRepository.save(user);
        log.info(LogMessages.REGISTER_USER_SAVED);
        String token = jwtService.generateToken(user.getName());
        log.info(LogMessages.REGISTER_TOKEN_GENERATED);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new RegisterResponseDto("User registered successfully", token));
    }


    @PostMapping(path = Endpoints.LOGIN, produces = Endpoints.PRODUCES)
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info(LogMessages.LOGIN_METHOD_TRIGGERED);
        User user = userRepository.findByName(loginRequestDto.getName())
                .orElse(null);

        ResponseEntity<String> checkUsernameOrPassword = authValidator.checkIfUsernameOrPasswordIsEmptyForLoginMethod(user);
        if (checkUsernameOrPassword != null) return checkUsernameOrPassword;
        log.info(LogMessages.LOGIN_USERNAME_NOT_FOUND);

        ResponseEntity<String> CheckPasswordEncoder = authValidator.CheckIfUsernameOrPasswordIsInvalidForPasswordEncoderForLoginMethod(loginRequestDto, user, passwordEncoder);
        if (CheckPasswordEncoder != null) return CheckPasswordEncoder;
        log.info(LogMessages.LOGIN_INVALID_CREDENTIALS);
        String token = jwtService.generateToken(user.getName());
        log.info(LogMessages.LOGIN_TOKEN_GENERATED);

        return ResponseEntity.ok(new LoginResponseDto(LogMessages.LOGIN_SUCCESS, token));
    }
}
