package org.example.playground.domain.question.service;

import jakarta.validation.Valid;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.springframework.transaction.annotation.Transactional;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionResponseDTO;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    //질문 생성
    @Transactional
    public QuestionResponseDTO create(QuestionCreateRequestDTO request) {
        Question question = Question.create(request.member_id(), request.title(), request.content());
        return QuestionResponseDTO.from(questionRepository.save(question));
    }

    //질문 상세 조회
    @Transactional(readOnly = true)
    public QuestionResponseDTO findOne(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("질문 없음: " + id)); //임시예외
        return QuestionResponseDTO.from(question);
    }

    //질문 수정
    @Transactional
    public QuestionResponseDTO update(Long id, QuestionUpdateRequestDTO request) {
        Question question = questionRepository.findById(id)
                .orElse(null); //todo 예외로직 추가 예쩡

        question.update(request.title(), request.content());
        return QuestionResponseDTO.from(question);
    }

    //질문 삭제
    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }
}
