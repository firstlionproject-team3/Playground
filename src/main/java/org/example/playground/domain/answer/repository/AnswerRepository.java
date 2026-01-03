package org.example.playground.domain.answer.repository;

import org.example.playground.domain.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 해당 질문의 답변 목록 - 최신순
    Page<Answer> findByQuestion_IdOrderByCreatedAtDesc(Long questionId, Pageable pageable);

    // 마이페이지 내가 작성한 답변 목록 조회용 - 최신순
    Page<Answer> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 질문에 달린 답변 목록 조회 (정렬 없음)
    List<Answer> findByQuestion_Id(Long questionId);

    //중복 채택 방지: 해당 질문에 accepted=true 답변 존재 여부
    boolean existsByQuestion_IdAndAcceptedTrue(Long questionId);

    //정렬: 채택된 답변 우선 + 최신순
    List<Answer> findByQuestion_IdOrderByAcceptedDescCreatedAtDesc(Long questionId);

    Page<Answer> findByQuestion_IdOrderByAcceptedDescCreatedAtDesc(Long questionId, Pageable pageable);

}
