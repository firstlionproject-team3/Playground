package org.example.playground.domain.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionDetailResponseDTO;
import org.example.playground.domain.question.dto.response.QuestionSummaryResponseDTO;
import org.example.playground.domain.question.service.QuestionService;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    //질문 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDetailResponseDTO create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody QuestionCreateRequestDTO request) {
        System.out.println("questioncontroller log");
        return questionService.create(principal.getId(), request);
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

    //질문 수정 - 작성자만
    @PatchMapping("/{id}")
    public QuestionDetailResponseDTO update(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody QuestionUpdateRequestDTO request)
    {
        return questionService.update(id, principal.getId(), request);
    }

    //질문 삭제 - 작성자만
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        questionService.delete(id,  principal.getId());
    }

    //질문 신고
    @PatchMapping("/{id}/report")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void report(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        questionService.report(id, principal.getId());
    }
    
    //질문에 답변 등록 알림 - 답변 도메인에서 "답변이 달렸다!" 이벤트를 만들고 여기에(이용자에게) 알림을 보냄 (AnswerService에서)


    @PatchMapping("/questions/{questionId}/answers/{answerId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        questionService.acceptAnswer(questionId, answerId, principal.getId());
    }


}
