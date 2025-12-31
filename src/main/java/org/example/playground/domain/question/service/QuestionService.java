package org.example.playground.domain.question.service;

import jakarta.validation.Valid;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionDetailResponseDTO;
import org.example.playground.domain.question.dto.response.QuestionSummaryResponseDTO;
import org.example.playground.domain.question.exception.QuestionNotFoundException;
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
    public QuestionDetailResponseDTO create(QuestionCreateRequestDTO request) {
        Question question = Question.create(request.memberId(), request.title(), request.content());
        return QuestionDetailResponseDTO.from(questionRepository.save(question));
    }

    //질문 전체목록 보기
    @Transactional(readOnly = true)
    public Page<QuestionSummaryResponseDTO> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(QuestionSummaryResponseDTO::from);
    }


    //질문 검색
    @Transactional(readOnly = true)
    public Page<QuestionSummaryResponseDTO> search(String type, String keyword, Pageable pageable) {
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
        return page.map(QuestionSummaryResponseDTO::from);
    }

/*
    로그인한 사용자가 자기가 작성한 질문 목록을 조회
    @Transactional(readOnly = true)
    public Page<QuestionSummaryResponseDTO> getMyQuestions(Long memberId, Pageable pageable) {
        //최신순
        Page<Question> page = questionRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return page.map(QuestionSummaryResponseDTO::from);
    }
    
 */

    //질문 1건 상세 조회
    @Transactional(readOnly = true)
    public QuestionDetailResponseDTO findOne(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        return QuestionDetailResponseDTO.from(question);
    }

    //질문 수정
    @Transactional
    public QuestionDetailResponseDTO update(Long id, QuestionUpdateRequestDTO request) {
        Question question = questionRepository.findById(id)
                .orElse(null); //todo 예외로직 추가 예정

        question.update(request.title(), request.content());
        return QuestionDetailResponseDTO.from(question);
    }

    //질문 삭제
    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }
}
