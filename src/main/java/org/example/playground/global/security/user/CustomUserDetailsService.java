package org.example.playground.global.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    /*
        추후에 UserRepository import 되면 주석해제

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "에 해당하는 회원이 존재하지 않습니다."));

        List<GrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();

        return new CustomUserDetails(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
               roles
        );
        */


        return null;
    }
}
