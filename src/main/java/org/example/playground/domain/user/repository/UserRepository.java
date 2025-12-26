package org.example.playground.domain.user.repository;

import org.example.playground.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);
    boolean existsByProviderAndProviderId(String provider,String providerId);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
