package org.example.playground.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.user.entity.User;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
//유저 요약 보기 (게시글, 댓글 에 나오는 정보)
public class UserPostAnswerResponseDTO {
    private String nickname;

    public static UserPostAnswerResponseDTO userPostAnswerResponseDTOFromEntity(User user){
        return UserPostAnswerResponseDTO.builder()
                .nickname(user.getNickname())
                .build();
    }
}
