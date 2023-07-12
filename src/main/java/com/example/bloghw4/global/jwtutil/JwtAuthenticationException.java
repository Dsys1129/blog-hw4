package com.example.bloghw4.global.jwtutil;

public class JwtAuthenticationException extends RuntimeException{
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
