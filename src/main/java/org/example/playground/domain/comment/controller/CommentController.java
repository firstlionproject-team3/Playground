package org.example.playground.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.comment.dto.request.CommentRequestDTO;
import org.example.playground.domain.comment.dto.response.CommentResponseDTO;
import org.example.playground.domain.comment.entity.Comment;
import org.example.playground.domain.comment.service.CommentService;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/answers/{answerId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody @Valid CommentRequestDTO commentRequestDTO,
                                                            @PathVariable Long answerId,
                                                            @AuthenticationPrincipal CustomUserDetails principal) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(commentRequestDTO, principal.getId(), answerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDTO);
    }

    // 댓글 조회
    @GetMapping("/answers/{answerId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComment(@PathVariable Long answerId) {
        return ResponseEntity.ok(commentService.getComments(answerId));
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable Long commentId,
                                                            @RequestBody CommentRequestDTO commentRequestDTO) {
        return ResponseEntity.ok(commentService.updateComment(commentRequestDTO, commentId));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 신고

    // 댓글 추천

    // 댓글 비추천
}
