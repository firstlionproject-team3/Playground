package org.example.playground.domain.question.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.playground.domain.answer.dto.response.AnswerResponseDTO;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.comment.entity.Comment;
import org.example.playground.domain.question.entity.Question;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    private long viewCount;
    private int likeCount;
    private int dislikeCount;
    private List<AnswerResponseDTO> answers;

    public static QuestionResponseDTO from(Question question, List<Answer> answers, List<Comment> comments) {
        // comment를 answerId 기준으로 그룹핑
        Map<Long, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getAnswer().getId()));

        List<AnswerResponseDTO> answerDTOs = answers.stream()
                .map(a -> AnswerResponseDTO.from(
                        // 답변이 있는데 댓글은 0개다 그러면 빈 리스트 반환
                        a, commentMap.getOrDefault(a.getId(), List.of())
                )).toList();

        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .nickname(question.getUser().getNickname()) // 쿼리 한 번
                .viewCount(question.getViewCount())
                .likeCount(question.getLikeCount())
                .dislikeCount(question.getDislikeCount())
                .answers(answerDTOs)
                .build();
    }

}
