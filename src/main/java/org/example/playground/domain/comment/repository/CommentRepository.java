package org.example.playground.domain.comment.repository;

import org.example.playground.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnswer_IdOrderByCreatedAtAsc(Long answerId);

    List<Comment> findByAnswerIdInOrderByCreatedAtAsc(List<Long> answerIds);

    // User fetch join
    @Query("""
        select c
        from Comment c
        join fetch c.user
        where c.answer.id in :answerIds
        order by c.createdAt asc
""")
    List<Comment> findByAnswerIdInWithUserOrderByCreatedAtAsc(List<Long> answerIds);
}
