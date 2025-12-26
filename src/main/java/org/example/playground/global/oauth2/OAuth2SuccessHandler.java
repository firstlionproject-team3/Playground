package org.example.playground.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    //TODO: 더미 서비스임 추후에 교체할 것.
    private final UserService userService;

    //TODO: 핸들러 작성 완료시 시큐리티 컨피그에서 oauth2Login 코드 수정

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //유저 정보 획득
        CustomOAuth2User socialUser = (CustomOAuth2User) authentication.getPrincipal();

        //이후 비지니스 흐름
        //소셜어카운트 서비스. provider + providerId 의 조합으로 엔티티 찾아오기.
        String providerId = socialUser.getProviderId();
        String provider =  socialUser.getProvider();

        //TODO: 추가 필요
        //boolean isSocialUser = userService.isRegisteredSocialUser(provider, providerId);

        //소셜유저 찾기 성공
        boolean isSocialUser = true;
        
        //소셜유저 찾기 실패
        //boolean isSocialUser = false;

        if (isSocialUser) {
            //DB 조회 성공 (로그인 진행) -> 정식 JWT 발급 -> 리다이렉트 홈페이지.

            //정식 JWT 토큰 발급

            //리다이렉트 url
            getRedirectStrategy().sendRedirect(request, response, "/로그인성공후도착url");
        } else{
            //DB 조회 실패 (회원가입 진행)

            //-> 내부 회원가입 로직( 알아서 내부에서 권한, 필수정보 생성) -> 필수 정보 : 권한, 말고없나??

            //TODO: 하드코딩 말고 잘 넣어서 조작하기
            String loginId = socialUser.getProvider() + "_" + socialUser.getProviderId();
            String nickName = socialUser.getProvider() + "난수38471";
            String password = "난수38ㄱ382";

            //UserDTO 생성 : 김한울 담당

            //소셜 회원가입
            //userService.registerSocialUser(userSocialDTO);

            //정식 JWT 발급 후 homurl 리다이렉트

            getRedirectStrategy().sendRedirect(request, response, "/homeURL");
        }


        //폐기됨.
        //-> 임시 JWT 발급(여기에 프로바이더 프로바이더ID 넣어야함) -> 회원가입 폼에서 다시 입력한 후 소셜 회원가입 API 에 요청 후 회원가입 진행 -> 회원가입 성공
        //임시 JWT 발급

    }
}
