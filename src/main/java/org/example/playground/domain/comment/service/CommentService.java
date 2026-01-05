package org.example.playground.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.answer.exception.AnswerErrorCode;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.comment.dto.request.CommentRequestDTO;
import org.example.playground.domain.comment.dto.response.CommentResponseDTO;
import org.example.playground.domain.comment.entity.Comment;
import org.example.playground.domain.comment.exception.CommentErrorCode;
import org.example.playground.domain.comment.exception.CommentException;
import org.example.playground.domain.comment.repository.CommentRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserErrorCode;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    // 댓글 생성
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO,
                                            Long userId, Long answerId) {
        //TODO: 나중에 UserErrorCode 넣어줘야함.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(AnswerErrorCode.ANSWER_NOT_FOUND));

        Comment createComment = Comment.create(commentRequestDTO.getContent(), user, answer);

        Comment comment = commentRepository.save(createComment);
        return CommentResponseDTO.from(comment);
    }

    // 댓글 단건 조회
    @Transactional(readOnly = true)
    public Comment getComment(Long answerId) {
        return commentRepository.findById(answerId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getComments(Long answerId) {
        return commentRepository.findByAnswer_IdOrderByCreatedAtAsc(answerId).stream()
                .map(CommentResponseDTO::from)
                .toList();
    }

    // 댓글 수정
    public CommentResponseDTO updateComment(CommentRequestDTO commentRequestDTO, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        comment.update(commentRequestDTO.getContent());

        return CommentResponseDTO.from(comment);
    }

    // 댓글 삭제 - 버튼을 작성자만 보이게하면 어떨까
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }

    // 소프트삭제 -> 관리자에 의해 삭제된 댓글입니다. 로 변경
    // 소프트삭제되면 수정,신고,추천,비추천 버튼 화면에서 없애기?
    public void softDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        comment.softDelete();
    }

    // 댓글 존재여부
    @Transactional(readOnly = true)
    public boolean existsByCommentId(Long commentId) {
        return commentRepository.existsById(commentId);
    }
}
