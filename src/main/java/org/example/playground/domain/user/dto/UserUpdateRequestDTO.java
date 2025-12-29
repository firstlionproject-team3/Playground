package org.example.playground.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
// Update 요청하는 dto
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateRequestDTO {
    @NotBlank
    @Size(min = 10, max = 100)
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]+$",
            message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다."
    )
    private String name;
    @NotBlank
    @Size(max = 100)
    @Email
    private String email;
}
