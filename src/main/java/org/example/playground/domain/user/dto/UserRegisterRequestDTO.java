package org.example.playground.domain.user.dto;

import jakarta.validation.constraints.Email;
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
public class UserRegisterRequestDTO {
    @Size(min = 10, max = 100)
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]+$",
            message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다."
    )
    private String name;
    @NotBlank
    @Size(max = 100)
    @Pattern( // 특수문자 사용 불가
            regexp = "^[a-zA-Z0-9]+$",
            message = "아이디는 영문자와 숫자만 사용할 수 있습니다."
    )
    private String loginId;
    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])[^\\s]{8,64}$",
            message = "비밀번호는 8~64자이며 영문, 숫자, 특수문자를 각각 최소 1개 포함하고 공백을 사용할 수 없습니다."
    )
    private String password;
    @Size(max = 100)
    @Email
    private String email;
}
