package org.example.playground.domain.comment.repository;

import org.example.playground.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnswer_IdOrderByCreatedAtAsc(Long answerId);
}
