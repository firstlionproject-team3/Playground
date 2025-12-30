package org.example.playground.global.security.user;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.SecurityResponseForJWT;
import org.example.playground.domain.user.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SecurityResponseForJWT dto = userService.handleLogin(username);
        Long id = dto.getId();
        Collection<? extends GrantedAuthority> roles = dto.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new CustomUserDetails(id, roles);
    }
}
