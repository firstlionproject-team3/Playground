package org.example.playground.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.dto.request.AnswerCreateRequestDTO;
import org.example.playground.domain.answer.dto.request.AnswerUpdateRequestDTO;
import org.example.playground.domain.answer.dto.response.AnswerDetailResponseDTO;
import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.answer.service.AnswerService;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AnswerController {
    private final AnswerService answerService;

    //해당 질문 답변 조회 전체
    //하나의 질문에 종속된 답변 목록들 전부 조회
    @GetMapping("/questions/{questionId}/answers")
    public Page<AnswerSummaryResponseDTO> getAnswers(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return answerService.getAnswerByQuestion(
                questionId,
                principal.getId(),
                pageable
        );
    }

    //답변 생성(등록)
    @PostMapping("/questions/{questionId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerDetailResponseDTO create(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody AnswerCreateRequestDTO request
            ) {
        return answerService.create(questionId, principal.getId(), request);
    }

    //답번 수정
    @PatchMapping("/answers/{answerId}")
    public AnswerDetailResponseDTO update(
            @PathVariable Long answerId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody AnswerUpdateRequestDTO request
    ) {
        return answerService.update(answerId, principal.getId(), request);
    }

    //답변 삭제
    @DeleteMapping("/answers/{answerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long answerId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        answerService.delete(answerId, principal.getId());
    }
}
