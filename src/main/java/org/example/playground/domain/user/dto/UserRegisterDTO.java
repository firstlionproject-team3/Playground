package org.example.playground.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
//회원가입 할때 받아오는 정보를 담는 DTO
public class UserRegisterDTO {
    @Size(max = 100)
    private String name;
    @NotBlank
    @Size(max = 100)
    @Pattern( // 특수문자 사용 불가
            regexp = "^[a-zA-Z0-9]+$",
            message = "아이디는 영문자와 숫자만 사용할 수 있습니다."
    )
    private String loginId;
    @NotBlank
    @Size(max = 20)
    private String password;
    @Size(max = 100)
    private String email;
}
