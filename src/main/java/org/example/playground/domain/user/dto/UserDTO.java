package org.example.playground.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class UserDTO {
    //회원가입 할때 받아오는 정보를 담는 DTO
    private Long id;
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 50)
    private String loginId; // provider_providerId
    @NotBlank
    @Size(max = 20)
    private String password;
    @Size(max = 100)
    private String email;
}
