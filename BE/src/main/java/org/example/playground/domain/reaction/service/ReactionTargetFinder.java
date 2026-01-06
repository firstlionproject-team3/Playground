package org.example.playground.domain.reaction.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.exception.AnswerErrorCode;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.comment.exception.CommentErrorCode;
import org.example.playground.domain.comment.repository.CommentRepository;
import org.example.playground.domain.question.exception.QuestionErrorCode;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.example.playground.domain.reaction.entity.ReactionCountable;
import org.example.playground.domain.reaction.entity.TargetType;
import org.example.playground.global.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionTargetFinder {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public ReactionCountable find(TargetType type, Long targetId) {
        return (ReactionCountable) switch (type) {
            case COMMENT -> commentRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(
                            CommentErrorCode.COMMENT_NOT_FOUND,
                            "해당하는 댓글을 찾을 수 없습니다. commentId = " + targetId
                    ));
            case ANSWER -> answerRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(
                            AnswerErrorCode.ANSWER_NOT_FOUND,
                            "해당하는 답변을 찾을 수 없습니다. answerId = " + targetId
                    ));
            case QUESTION -> questionRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(
                            QuestionErrorCode.QUESTION_NOT_FOUND,
                            "해당하는 질문을 찾을 수 없습니다. questionId = " + targetId
                    ));
        };
    }
}
