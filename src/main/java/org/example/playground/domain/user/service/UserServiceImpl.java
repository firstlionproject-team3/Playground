package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.entity.Role;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.AnotherUserException;
import org.example.playground.domain.user.exception.DuplicateUserException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.RoleRepository;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.domain.user.repository.UserRoleRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.playground.domain.user.dto.UserRegisterDTO.userRegisterDTOfromEntity;
import static org.example.playground.domain.user.entity.User.userFromDTO;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserRegisterDTO createUser(UserDTO userDTO) {
        if(userRepository.existsByLoginId(userDTO.getLoginId())){
            throw new DuplicateUserException("이미 존재하는 로그인 ID입니다");
        }

        // 비밀번호 인코딩 과정
        // DB에 넣을 인스턴스 가공
        String encodingPW = passwordEncoder.encode(userDTO.getPassword());
        User user = userFromDTO(userDTO, encodingPW);

        //기본적으로 USER 권한 부여. 만약 roles 테이블에 USER 이 없을 시 새로 만들어서 USER 부여. (첫 회원)
        user.addRole(roleRepository.findByName("USER").orElseGet(()
                -> roleRepository.save(new Role("USER"))));

        return userRegisterDTOfromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id, UserDetails currentUser) {
        User findUser = userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        // 권한 검증
        if(!(currentUser.getAuthorities().stream().anyMatch(auth -> auth
                .getAuthority().equals("ROLE_ADMIN")) || findUser.getLoginId().equals(currentUser.getUsername()))){
            throw new AnotherUserException("본인 혹은 관리자만 회원 탈퇴를 진행할 수 있습니다.");
        }

        userRepository.delete(findUser);
    }
}

