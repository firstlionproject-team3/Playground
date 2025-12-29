package org.example.playground.domain.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionResponseDTO;
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
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponseDTO create(@Valid @RequestBody QuestionCreateRequestDTO request) {
        return questionService.create(request);
    }

    //질문 검색 - keyword 없으면 전체조회, 있으면 title,content,all검색(서비스에서 처리)
    @GetMapping
    public Page<QuestionResponseDTO> getQuestions(
            //요청에 size가 없으면 한 페이지에 기본 10개씩 내려주기
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword
    ) {
        return questionService.search(type, keyword, pageable);
    }

    //질문 상세 조회(1건)
    @GetMapping("/{id}")
    public QuestionResponseDTO one(@PathVariable Long id) {
        return questionService.findOne(id);
    }

    //질문 수정
    @PatchMapping("/{id}")
    public QuestionResponseDTO update(@PathVariable Long id, @Valid @RequestBody QuestionUpdateRequestDTO request) {
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
