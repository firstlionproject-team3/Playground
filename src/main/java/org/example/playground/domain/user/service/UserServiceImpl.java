package org.example.playground.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.UserDTO;
import org.example.playground.domain.user.dto.UserRegisterDTO;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.DuplicateUserException;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
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
//    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserRegisterDTO createUser(UserDTO userDTO) {
        if(userRepository.existsByLoginId(userDTO.getLoginId())){
            throw new DuplicateUserException("이미 존재하는 로그인 ID입니다");
        }

        //TODO 비밀번호 인코딩 과정
        //DB에 넣을 인스턴스 가공
//        String encodingPW = passwordEncoder.encode(userDTO.getPassword());
        User user = userFromDTO(userDTO, userDTO.getPassword());

        return userRegisterDTOfromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User findUser = userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        //TODO 권한 검증

        userRepository.delete(findUser);
    }
}
