package org.example.playground.global.oauth2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeInfo {
    private Long userId;
    private List<String> roles;
    private long createdAt;


    public static CodeInfo create(Long userId, List<String> roles) {
        return CodeInfo.builder()
                .userId(userId)
                .roles(roles)
                .createdAt(System.currentTimeMillis())
                .build();
    }


}
