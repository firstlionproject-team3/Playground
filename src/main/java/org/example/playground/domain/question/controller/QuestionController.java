package org.example.playground.domain.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionDetailResponseDTO;
import org.example.playground.domain.question.dto.response.QuestionResponseDTO;
import org.example.playground.domain.question.dto.response.QuestionSummaryResponseDTO;
import org.example.playground.domain.question.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    //질문 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //무조건 200
    public QuestionDetailResponseDTO create(@Valid @RequestBody QuestionCreateRequestDTO request) {
        return questionService.create(request);
    }

    //질문 검색 - keyword 없으면 전체조회, 있으면 title,content,all검색(서비스에서 처리)
    @GetMapping
    public Page<QuestionSummaryResponseDTO> getQuestions(
            //요청에 size가 없으면 한 페이지에 기본 10개씩 내려주기
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword
    ) {
        return questionService.search(type, keyword, pageable);
    }

    //질문 상세 조회(1건) //responseBody로 json으로 변환 - 프론트에 넘겨줌,
    //responseentity라는 스프링이 자체적으로 갖고있는 클래스가있음
    @GetMapping("/{id}")
    public QuestionDetailResponseDTO one(@PathVariable Long id) {
        return questionService.findOne(id);
    }

    //질문 수정
    @PatchMapping("/{id}")
    public QuestionDetailResponseDTO update(@PathVariable Long id, @Valid @RequestBody QuestionUpdateRequestDTO request) {
        return questionService.update(id, request);
    }

    //질문 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        questionService.delete(id);
    }

    //질문 신고
    @PatchMapping("/{id}/report")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void report(@PathVariable Long id) {
        //todo 질문 신고정책 설계 필요
    }

}
