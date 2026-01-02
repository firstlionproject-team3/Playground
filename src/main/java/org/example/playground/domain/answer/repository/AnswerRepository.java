package org.example.playground.domain.answer.repository;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer,Long> {

    //해당 질문의 답변 목록 - 최신순
    Page<Answer> findByQuestion_IdOrderByCreatedAtDesc(Long questionId, Pageable pageable);

    //마이페이지 내가 작성한 답변 목록 조회용 - 최신순
    Page<Answer> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
