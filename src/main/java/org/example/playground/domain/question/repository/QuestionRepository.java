package org.example.playground.domain.question.repository;

import org.example.playground.domain.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    //전체조회
    Page<Question> findAll(Pageable pageable);

    //키워드조회(제목이나 본문)

}
