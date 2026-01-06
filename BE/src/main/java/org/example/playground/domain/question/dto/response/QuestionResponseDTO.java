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
    private Long userId;
    private String title;
    private String content;
    private String nickname;
    private long viewCount;
    private int likeCount;
    private int dislikeCount;
    private String myReactionType; // LIKE | DISLIKE | NONE
    private List<AnswerResponseDTO> answers;

    public static QuestionResponseDTO from(
            Question question, 
            List<Answer> answers, 
            List<Comment> comments,
            String myReactionType,
            Map<Long, String> answerMyReactionMap,
            Map<Long, String> commentMyReactionMap
    ) {
        // comment를 answerId 기준으로 그룹핑
        Map<Long, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getAnswer().getId()));

        List<AnswerResponseDTO> answerDTOs = answers.stream()
                .map(a -> {
                    List<Comment> answerComments = commentMap.getOrDefault(a.getId(), List.of());
                    return AnswerResponseDTO.from(
                            a, 
                            answerComments,
                            answerMyReactionMap.getOrDefault(a.getId(), "NONE"),
                            commentMyReactionMap
                    );
                }).toList();

        return QuestionResponseDTO.builder()
                .id(question.getId())
                .userId(question.getUser().getId())
                .title(question.getTitle())
                .content(question.getContent())
                .nickname(question.getUser().getNickname()) // 쿼리 한 번
                .viewCount(question.getViewCount())
                .likeCount(question.getLikeCount())
                .dislikeCount(question.getDislikeCount())
                .myReactionType(myReactionType != null ? myReactionType : "NONE")
                .answers(answerDTOs)
                .build();
    }

}
