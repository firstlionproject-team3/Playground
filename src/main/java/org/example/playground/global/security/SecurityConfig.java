package org.example.playground.global.security;

import org.example.playground.global.oauth2.OAuth2FailureHandler;
import org.example.playground.global.oauth2.OAuth2SuccessHandler;
import org.example.playground.global.security.jwt.JwtTokenFilter;
import org.example.playground.global.security.jwt.exception.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource configurationSource;

    // 순환 참조 방지, 의도적으로 생성자 작성
    public SecurityConfig(@Lazy OAuth2SuccessHandler oAuth2SuccessHandler, OAuth2FailureHandler oAuth2FailureHandler, JwtTokenFilter jwtTokenFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, CorsConfigurationSource configurationSource) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.configurationSource = configurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth -> {
                    oauth.successHandler(oAuth2SuccessHandler);
                    oauth.failureHandler(oAuth2FailureHandler);
                })
                // TODO: h2 콘솔 허용 설정 추후 교체
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                )
                // 폼 로그인 비활성화 (세션 안쓰고 JWT 사용)
                .formLogin(form -> form.disable())
                // HTTP Basic 인증 비활성화
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용할 API
                        // 회원가입, 일반로그인, 토큰 재발급
                        .requestMatchers("/users", "/auth/login", "/user/refreshToken").permitAll()
                        // OAuth 관련
                        .requestMatchers("/oauth2/authorization/**","/login/oauth2/code/**").permitAll()
                        // h2 콘솔 - 테스트용
                        .requestMatchers("/h2-console/**").permitAll()
                        // 인증된 사용자만 접근 가능
                        // (Authorization 헤더에 유효한 accessToken 필요)
                        .anyRequest().authenticated())

                // 세션을 사용하지않는 STATELESS 구조 -> 매 요청마다 JWT로 인증
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // cors 설정 적용 - 다른 출처에서의 요청을 허용
                .cors(cors -> cors.configurationSource(configurationSource))
                // JWT 인증 필터 등록
                // UsernamePasswordAuthenticationFilter 전에 실행되어
                // 요청마다 accessToken을 검증하고 SecurityContext에 인증 정보 저장
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 인증 실패(토큰 만료, 위조 등) 시 처리할 EntryPoint 설정
                // 401 응답 + JSON 에러 메시지 반환
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}