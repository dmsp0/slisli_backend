package com.quest_exfo.backend.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private String email;
    private String name;
    private String password;
    private Long member_id;
    private String profileImgPath;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String email, String name, String password, Long member_id, String profileImgPath,Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.member_id = member_id;
        this.profileImgPath = profileImgPath;
        this.authorities = authorities;
    }

    public String getName() {
        return name;
    }

    public Long getMember_id() {
        return member_id;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}