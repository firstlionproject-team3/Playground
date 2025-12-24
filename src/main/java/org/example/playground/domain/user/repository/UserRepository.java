package org.example.playground.domain.user.repository;

import org.example.playground.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLoginId(String loginId);

}
