package org.example.playground.domain.user.repository;

import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserRole;
import org.example.playground.domain.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    //회원 DB에 등록되어있는지 확인용
    boolean existsByLoginId(String loginId);
    boolean existsByProviderAndProviderId(String provider,String providerId);
    boolean existsByIdAndStatus(Long id, UserStatus status);

    //OAuth2 로그인 회원만 검색
    Optional<User> findUserByProviderAndProviderId(String provider, String providerId);

    //로그인 아이디로 회원 검색(회원정보 상세조회)
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByLoginIdAndStatus(String loginId, UserStatus status);
}
