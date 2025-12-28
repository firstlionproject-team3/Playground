package org.example.playground.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
//유저 요약 보기 (게시글, 댓글 에 나오는 정보)
public class UserSummaryDTO {
    private Integer id;
    private String name;
}
