package com.example.bloghw4.jwtutil;

import com.example.bloghw4.user.entity.UserRole;

import lombok.Getter;

@Getter
public class UserDetails {

    private String username;

    private UserRole userRole;

    public UserDetails(String username, UserRole userRole) {
        this.username = username;
        this.userRole = userRole;
    }
}
