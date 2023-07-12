package com.example.bloghw4.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.global.jwtutil.JwtProvider;
import com.example.bloghw4.user.dto.LoginResponseDTO;
import com.example.bloghw4.user.dto.RefreshTokenResponseDTO;
import com.example.bloghw4.user.dto.UserRequestDTO;
import com.example.bloghw4.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponseDTO> signup(@Valid @RequestBody UserRequestDTO userRequestDTO){
        BaseResponseDTO response = userService.signup(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // @PostMapping("/login")
    public ResponseEntity<BaseResponseDTO> login(@Valid @RequestBody UserRequestDTO userRequestDTO){
        LoginResponseDTO loginResponse = userService.login(userRequestDTO);
        BaseResponseDTO responseBody = new BaseResponseDTO(loginResponse.getMsg(), loginResponse.getStatus());
        return ResponseEntity.status(HttpStatus.OK)
                .header(JwtProvider.AUTHORIZATION_HEADER, loginResponse.getAccessToken())
                .header(JwtProvider.REFRESH_TOKEN_HEADER, loginResponse.getRefreshToken())
                .body(responseBody);
    }

    @GetMapping("/reissue")
    public ResponseEntity<BaseResponseDTO> refreshToken(@RequestHeader(JwtProvider.REFRESH_TOKEN_HEADER) String refreshToken) {
        RefreshTokenResponseDTO refreshTokenResponseDTO = userService.refreshToken(refreshToken);
        BaseResponseDTO responseBody = new BaseResponseDTO(refreshTokenResponseDTO.getMsg(), refreshTokenResponseDTO.getStatus());
        return ResponseEntity.status(HttpStatus.OK)
                .header(JwtProvider.AUTHORIZATION_HEADER, refreshTokenResponseDTO.getAccessToken())
                .body(responseBody);
    }
}
