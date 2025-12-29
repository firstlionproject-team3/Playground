package org.example.playground.domain.question.repository;

import org.example.playground.domain.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    //제목 검색
    Page<Question> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    //본문검색
    //긴글이고 TEXT이기 때문에 IgnoreCase(UPPER()) 사용이 안됨..!
    Page<Question> findByContentContaining(String content, Pageable pageable);

    //제목+본문검색
    Page<Question> findByTitleContainingIgnoreCaseOrContentContaining(
            String title, String content, Pageable pageable);

}
