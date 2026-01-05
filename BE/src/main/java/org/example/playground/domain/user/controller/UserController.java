package org.example.playground.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.answer.service.AnswerService;
import org.example.playground.domain.question.dto.response.QuestionSummaryResponseDTO;
import org.example.playground.domain.question.service.QuestionService;
import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.dto.UserRegisterRequestDTO;
import org.example.playground.domain.user.dto.UserRegisterResponseDTO;
import org.example.playground.domain.user.dto.UserUpdateRequestDTO;
import org.example.playground.domain.user.service.UserService;
import org.example.playground.global.security.user.CustomUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    //회원 가입
    @PostMapping
    public ResponseEntity<UserRegisterResponseDTO> createUser(
            @Valid @RequestBody UserRegisterRequestDTO userDTO
    ) {
        UserRegisterResponseDTO user = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // GET /me (마이페이지)
    @GetMapping("/me")
    public ResponseEntity<UserMyPageResponseDTO> myPage(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getUser(userDetails.getId()));
    }

    // PATCH /me (마이페이지 정보수정)
    @PatchMapping("/me")
    public ResponseEntity<UserMyPageResponseDTO> myPageUpdate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO
            ){
        return ResponseEntity.ok(userService.updateUser(userDetails.getId(), userUpdateRequestDTO));
    }

    // GET /me/questions?page=0&size=20 (마이페이지 - 질문 목록)
    @GetMapping("/me/questions")
    public ResponseEntity<Page<QuestionSummaryResponseDTO>> getMyQuestions(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(questionService.getMyQuestions(userDetails.getId(), pageable));
    }

    // GET /me/answers?page=0&size=20 (마이페이지 에서 내가 쓴 댓글 클릭시 - 댓글 목록)
    @GetMapping("/me/answers")
    public ResponseEntity<Page<AnswerSummaryResponseDTO>> getMyAnswers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(answerService.getMyAnswers(userDetails.getId(), pageable));
    }

    //마이페이지에서 삭제
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        userService.deleteUser(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
