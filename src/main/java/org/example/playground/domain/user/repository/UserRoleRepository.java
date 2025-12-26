package org.example.playground.domain.user.repository;

import org.example.playground.domain.user.entity.Role;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    //권한을 관리자가 직접 부여할때 이미 있는지 검증용
    boolean existsByUserAndRole(User user, Role role);
}
