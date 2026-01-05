package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.repository.RoleRepository;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTxService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * 저장 + flush 1회 시도는 항상 새 트랜잭션에서 수행
     */
    // 기본 ROLE_USER를 부여한 뒤 저장(즉시 flush하여 유니크 위반을 현재 스코프에서 감지)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User saveUserWithDefaultRoleNewTx(User user) {
        addUserRole(user);
        return userRepository.saveAndFlush(user);
    }

    //기본적으로 USER 권한 부여
    private void addUserRole(User user) {
        user.addRole(roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                new IllegalStateException("ROLE_USER가 존재하지 않습니다. DB 초기화 상태를 확인하세요.")));
    }
}