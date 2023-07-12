package com.example.bloghw4.global.jwtutil;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.bloghw4.global.security.CustomUserDetails;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean hasUserType = UserDetails.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasUserType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
            return new UserDetails(customUserDetails.getUser().getUsername(), customUserDetails.getUser().getUserRole());
        }
        return null;
    }
}

