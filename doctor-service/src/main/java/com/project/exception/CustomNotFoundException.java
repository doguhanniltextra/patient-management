package com.project.exception;


public class CustomNotFoundException extends RuntimeException  {

    public CustomNotFoundException() {
        super();
    }

    public CustomNotFoundException(String message) {
        super(message);
    }
}

