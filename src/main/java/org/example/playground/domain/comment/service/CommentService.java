package org.example.playground.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.answer.exception.AnswerNotFoundException;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.comment.dto.request.CommentRequestDTO;
import org.example.playground.domain.comment.dto.response.CommentResponseDTO;
import org.example.playground.domain.comment.entity.Comment;
import org.example.playground.domain.comment.exception.CommentNotFoundException;
import org.example.playground.domain.comment.repository.CommentRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("회원이 존재하지 않습니다."));
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        Comment createComment = Comment.create(commentRequestDTO.getContent(), user, answer);

        Comment comment = commentRepository.save(createComment);
        return CommentResponseDTO.from(comment);
    }

    // 댓글 단건 조회
    @Transactional(readOnly = true)
    public Comment getComment(Long answerId) {
        return commentRepository.findById(answerId)
                .orElseThrow(() -> new CommentNotFoundException("댓글이 존재하지 않습니다."));
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
                .orElseThrow(() -> new CommentNotFoundException("댓글이 존재하지 않습니다."));
        comment.update(commentRequestDTO.getContent());

        return CommentResponseDTO.from(comment);
    }


    // 댓글 삭제 - 버튼을 작성자만 보이게하면 어떨까
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }


}
