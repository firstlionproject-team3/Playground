package org.example.playground.global.security.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String loginId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String loginId, String email, List<GrantedAuthority> roles) {
        this.id = id;
        this.loginId = loginId;
        this.email = email;
        this.authorities = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return loginId;
    }
}
