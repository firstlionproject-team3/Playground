package org.example.playground.domain.reaction.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.exception.AnswerNotFoundException;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.question.exception.QuestionNotFoundException;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.example.playground.domain.reaction.entity.ReactionCountable;
import org.example.playground.domain.reaction.entity.TargetType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReactionTargetFinder {

//    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public ReactionCountable find(TargetType type, Long targetId) {
        return null;
//        return switch (type) {
//            case COMMENT -> commentRepository.findById(targetId)
//                    .orElseThrow(() -> new CommentNotFoundException(...));
//            case ANSWER -> answerRepository.findById(targetId)
//                    .orElseThrow(() -> new AnswerNotFoundException(targetId));
//            case QUESTION -> questionRepository.findById(targetId)
//                    .orElseThrow(() -> new QuestionNotFoundException(targetId));
//        };
    }
}
