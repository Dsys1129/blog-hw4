package com.example.bloghw4.global.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.bloghw4.global.jwtutil.JwtAuthenticationException;
import com.example.bloghw4.global.jwtutil.JwtProvider;
import com.example.bloghw4.global.security.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 검증, 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthorizationFilter running!");
        String tokenHeader = request.getHeader(JwtProvider.AUTHORIZATION_HEADER);
            String token = jwtProvider.substringToken(tokenHeader);

            jwtProvider.validateToken(token);

            String username = jwtProvider.getUserInfoFromToken(token).getSubject();
            try {
                setAuthentication(username);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new JwtAuthenticationException("인증 중 오류가 발생하였습니다.", e);
            }

        filterChain.doFilter(request,response);
    }

    // 필터 적용 X
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String servletPath = request.getRequestURI();
        return servletPath.startsWith("/api/auth") || servletPath.startsWith("/h2-console") ||
            (request.getMethod().equals("GET") && servletPath.startsWith("/api/posts"));
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
