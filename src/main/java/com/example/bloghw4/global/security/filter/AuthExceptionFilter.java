package com.example.bloghw4.global.security.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.bloghw4.global.jwtutil.JwtAuthenticationException;
import com.example.bloghw4.global.exception.dto.ExceptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "AuthExceptionFilter")
@RequiredArgsConstructor
public class AuthExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        log.info("AuthExceptionFilter running!");
        try {
            filterChain.doFilter(request,response);
        } catch (JwtAuthenticationException | NullPointerException | IllegalArgumentException e){
            handleException(response, HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void handleException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, String> errors = Collections.singletonMap("error", message);
        ExceptionDTO responseBody = new ExceptionDTO("false", status.value(), errors);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}

