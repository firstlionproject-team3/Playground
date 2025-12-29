package org.example.playground.global.security.user;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // userService.~~~
        // -> 여기서 userid, roles 가지고있는 dto 받아서 customUserDetails에 넣어서 반환

        // Collection<? extends GrantedAuthority> roles = user.getRoles().stream()
        //         .map(role -> new SimpleGrantedAuthority(role.getRole().getName()))
        //         .toList();
        //
        // return new CustomUserDetails(user.getId(), roles);

        return null;
    }
}
