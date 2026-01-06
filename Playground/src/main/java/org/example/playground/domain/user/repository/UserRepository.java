package org.example.playground.domain.user.repository;

import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //회원 DB에 등록되어있는지 확인용
    boolean existsByLoginId(String loginId);

    boolean existsByProviderAndProviderId(String provider, String providerId);

    boolean existsByIdAndStatus(Long id, UserStatus status);

    //OAuth2 로그인 회원만 검색
    Optional<User> findUserByProviderAndProviderId(String provider, String providerId);

    //로그인 아이디로 회원 검색(회원정보 상세조회)
    Optional<User> findByLoginIdAndStatus(String loginId, UserStatus status);

    //ACTIVE 인 관리자 전원의 id 값을 List로 반환하는 메서드
    @Query("""
            select distinct u.id
            from User u
            join u.roles ur
            join ur.role r
            where u.status = :status
              and r.name = :roleName
            """)
    List<Long> findActiveAdminIds(@Param("status") UserStatus status,
                                  @Param("roleName") String roleName);

    //ACTIVE 인 관리자인가? 를 확인하는 메서드
    @Query("""
                select case when count(u) > 0 then true else false end
                from User u
                where u.id = :userId
                  and u.status = 'ACTIVE'
                  and exists (
                      select 1
                      from UserRole ur
                      join ur.role r
                      where ur.user = u
                        and r.name = 'ROLE_ADMIN'
                  )
            """)
    boolean isActiveAdmin(@Param("userId") Long userId);
}
