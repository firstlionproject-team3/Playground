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

    //닉네임으로 질문 검색
    //이름에 keyword가 포함된 사람들을 기준으로 조회
    //
    Page<Question> findByMemberNicknameContainingIgnoreCase(String keyword, Pageable pageable);

    //특정회원이 작성한 질문들을 최신순으로 조회
    //정확히 누구의 글인가? 로그인아이디로 조회, db에서 딱 그사람을 가리키는 값, 닉네임으로 설정하면 변경했을때 예전 글이 안나오는 문제
    Page<Question> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

}
