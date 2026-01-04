package org.example.playground.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.entity.UserStatus;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.security.user.CustomUserDetails;
import org.example.playground.global.security.jwt.exception.JwtExceptionCode;
import org.example.playground.global.util.CookieUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 토큰을 얻어와서 검증한 후, 유효하다면
        // SecurityContextHolder에 Authentication 객체로 등록. ( 1회용, 매 요청이 끝나면 제거됨.)
        // Authentication 객체를 생성할 때 간단하게 UsernamePasswordAuthenticationToken을 사용해서 등록하면 편하고,
        // AbstractAuthenticationToken을 상속받는 클래스를 하나 정의하여 커스텀으로 넣어줄 수도 있다.

        // 1. 토큰 얻어오기
        String token = getToken(request);

        // 2. 토큰 파싱 -> Authentication 객체로 SecurityContextHolder에 저장.
        // 여기서 if문 통과한다면 토큰이 존재한다는 것 -> 만료된 토큰이나 유효하지 않은 토큰 등 포함.
        /* try 문에서 예외가 터진다면 SecurityContextHolder에 Authentication 객체가 저장되지 않았으므로
        인증이 실패하여 JwtAuthenticationEntryPoint의 commence가 호출된다.*/
        if (StringUtils.hasText(token)) {
            try {
                tokenToAuthentication(token);
            } catch (ExpiredJwtException e) { // 기간이 만료된 토큰
                request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                filterChain.doFilter(request, response);
                return;
            } catch (UnsupportedJwtException e){ // 지원하지 않는 토큰
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                filterChain.doFilter(request, response);
                return;
            } catch (MalformedJwtException e) { // 유효하지 않은 토큰
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) { // jwt 검증 중 예상하지못한 나머지 예외
                request.setAttribute("exception", JwtExceptionCode.UNKNOWN_ERROR.getCode());
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 토큰 -> SecurityContextHolder에 Authentication 객체로 저장.
    private void tokenToAuthentication(String token) {
        Claims claims = jwtTokenProvider.parseAccessToken(token);
        Long userId = Long.parseLong(claims.getSubject());

        boolean active = userRepository.existsByIdAndStatus(userId, UserStatus.ACTIVE);
        if(!active){
             throw new BadCredentialsException("이미 탈퇴한 회원입니다.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : (List<String>) claims.get("roles")) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        // 위의 정보를 UserDetails에 담아준다.
        CustomUserDetails customUserDetails = new CustomUserDetails(userId, authorities);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 토큰을 얻어오는 메서드 ( 2가지 방식 )
    private String getToken(HttpServletRequest request) {

        // 첫 번째 방식: Authorization 헤더에서 꺼내는 방식
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        // 두 번째 방식: 쿠키에서 꺼내는 방식: 반복문을 돌면서 토큰을 찾는다.
        // 찾지 못하면 null을 반환 -> 로그인, 회원가입 등의 요청이 될 수도 있다.
        return CookieUtil.getToken(request, "accessToken");
    }
}
