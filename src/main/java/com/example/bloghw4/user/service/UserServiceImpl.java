package com.example.bloghw4.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.jwtutil.JwtProvider;
import com.example.bloghw4.user.dto.LoginResponseDTO;
import com.example.bloghw4.user.dto.RefreshTokenResponseDTO;
import com.example.bloghw4.user.dto.UserRequestDTO;
import com.example.bloghw4.user.entity.RefreshToken;
import com.example.bloghw4.user.entity.User;
import com.example.bloghw4.user.exception.PasswordMismatchException;
import com.example.bloghw4.user.exception.RefreshTokenExpiredException;
import com.example.bloghw4.user.exception.UserDuplicationException;
import com.example.bloghw4.user.exception.UserNotFoundException;
import com.example.bloghw4.user.repository.RefreshTokenRepository;
import com.example.bloghw4.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    @Override
    public BaseResponseDTO signup(UserRequestDTO userRequestDTO) {
        Optional<User> findUser = userRepository.findByUsername(userRequestDTO.getUsername());

        if (findUser.isPresent()){
            throw new UserDuplicationException("중복된 username 입니다.");
        }
        User user = User.createUser(userRequestDTO.getUsername(), passwordEncoder.encode(userRequestDTO.getPassword()));
        userRepository.save(user);

        return new BaseResponseDTO("회원가입 성공",201);
    }

    @Transactional
    @Override
    public LoginResponseDTO login(UserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        String rawPassword = userRequestDTO.getPassword();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(rawPassword,user.getPassword())){
            throw new PasswordMismatchException("회원을 찾을 수 없습니다.");
        }
        String accessToken = jwtProvider.createAccessToken(username, user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(username, user.getUserRole());

        Optional<RefreshToken> exitRefreshToken = refreshTokenRepository.findById(user.getId());

        if(exitRefreshToken.isPresent()) {
            exitRefreshToken.get().updateToken(jwtProvider.substringToken(refreshToken));
        } else {
            RefreshToken newRefreshToken = new RefreshToken(user, jwtProvider.substringToken(refreshToken));
            refreshTokenRepository.save(newRefreshToken);
        }

        return new LoginResponseDTO("로그인 성공",200, accessToken, refreshToken);
    }

    @Override
    public RefreshTokenResponseDTO refreshToken(String refreshToken) {
        String token = jwtProvider.substringToken(refreshToken);
        jwtProvider.validateToken(token);
        RefreshToken exitRefreshToken = refreshTokenRepository.findByRefreshToken(token).orElseThrow(() ->
                new RefreshTokenExpiredException("Refresh token이 만료되었습니다. 로그인이 필요합니다."));

        Optional<User> user = userRepository.findById(exitRefreshToken.getUser().getId());
        if(user.isPresent()) {
            String accessToken = jwtProvider.createAccessToken(user.get().getUsername(), user.get().getUserRole());
            return new RefreshTokenResponseDTO("재발급 성공", 200, accessToken);
        } else {
            refreshTokenRepository.delete(exitRefreshToken);
            throw new UserNotFoundException("존재하지 않는 회원입니다.");
        }
    }
}
