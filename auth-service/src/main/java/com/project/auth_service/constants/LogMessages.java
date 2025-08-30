package com.project.auth_service.constants;

public class LogMessages {
    // ---- REGISTER ----
    public static final String REGISTER_METHOD_TRIGGERED = "AUTH: CONTROLLER - REGISTER - TRIGGERED";
    public static final String REGISTER_USERNAME_EXISTS = "AUTH: REGISTER - USERNAME_ALREADY_EXISTS";
    public static final String REGISTER_USER_SAVED = "AUTH: REGISTER - USER_SAVED - ID -> {}";
    public static final String REGISTER_TOKEN_GENERATED = "AUTH: REGISTER - TOKEN_GENERATED";

    // ---- LOGIN ----
    public static final String LOGIN_METHOD_TRIGGERED = "AUTH: CONTROLLER - LOGIN - TRIGGERED";
    public static final String LOGIN_USERNAME_NOT_FOUND = "AUTH: LOGIN - USERNAME_NOT_FOUND";
    public static final String LOGIN_INVALID_CREDENTIALS = "AUTH: LOGIN - INVALID_USERNAME_OR_PASSWORD";
    public static final String LOGIN_SUCCESS = "AUTH: LOGIN - SUCCESS - USER -> {}";
    public static final String LOGIN_TOKEN_GENERATED = "AUTH: LOGIN - TOKEN_GENERATED";

    // ---- AUTH VALIDATOR ----
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String CHECK_IF_USERNAME_OR_PASSWORD_IS_INVALID = "Invalid username or password";
    public static final String CHECK_IF_USERNAME_OR_PASSWORD_IS_EMPTY = "Invalid username or password";

}
