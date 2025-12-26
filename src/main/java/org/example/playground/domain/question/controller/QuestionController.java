package org.example.playground.domain.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionResponseDTO;
import org.example.playground.domain.question.service.QuestionService;
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

    //질문 상세 조회
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



}
