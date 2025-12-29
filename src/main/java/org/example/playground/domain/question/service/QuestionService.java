package org.example.playground.domain.question.service;

import jakarta.validation.Valid;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //질문 전체목록 보기
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(QuestionResponseDTO::from);
    }


    //질문 검색
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> search(String type, String keyword, Pageable pageable) {
        //keyword가 없으면 전체질문목록 보여주기
        if(keyword == null || keyword.isBlank()) {
            return getQuestions(pageable);
        }

        //type이 null이면
        String t;
        if(type == null) t = "all";
        else t = type.trim().toLowerCase();

        //type에 따라 repository 메서드 선택
        Page<Question> page;
        switch(t) {
            case "title":
                page =  questionRepository.findByTitleContainingIgnoreCase(keyword, pageable);
                break;
            case "content":
                page =  questionRepository.findByContentContaining(keyword, pageable);
                break;
            case "all":
            default:
                page = questionRepository.findByTitleContainingIgnoreCaseOrContentContaining(keyword, keyword, pageable);
                break;
        }
        //엔티티를 DTO로 변환 - 도움
        return page.map(QuestionResponseDTO::from);
    }


    //질문 1건 상세 조회
    @Transactional(readOnly = true)
    public QuestionResponseDTO findOne(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("질문 없음: " + id)); //todo 예외로직 추가 예쩡
        return QuestionResponseDTO.from(question);
    }

    //질문 수정
    @Transactional
    public QuestionResponseDTO update(Long id, QuestionUpdateRequestDTO request) {
        Question question = questionRepository.findById(id)
                .orElse(null); //todo 예외로직 추가 예정

        question.update(request.title(), request.content());
        return QuestionResponseDTO.from(question);
    }

    //질문 삭제
    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }
}
