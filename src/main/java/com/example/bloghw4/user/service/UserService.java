package com.example.bloghw4.user.service;

import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.user.dto.LoginResponseDTO;
import com.example.bloghw4.user.dto.RefreshTokenResponseDTO;
import com.example.bloghw4.user.dto.UserRequestDTO;

public interface UserService {

    BaseResponseDTO signup(UserRequestDTO userRequestDTO);

    LoginResponseDTO login(UserRequestDTO userRequestDTO);

    RefreshTokenResponseDTO refreshToken(String refreshToken);
}
