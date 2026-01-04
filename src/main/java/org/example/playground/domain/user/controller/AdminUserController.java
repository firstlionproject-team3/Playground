package org.example.playground.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.ForAdminDTO;
import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.service.AdminUserService;
import org.example.playground.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    //TODO 관리자 계정용 User목록 조회?
    private final AdminUserService adminUserService;
    private final UserService userService;

    //관리자의 유저 목록 보기
    @GetMapping
    public ResponseEntity<Page<ForAdminDTO>> getUserList(Pageable pageable) {
        Page<ForAdminDTO> page = adminUserService.getAllUsers(pageable)
                .map(ForAdminDTO::forAdminDTOFromEntity);
        return ResponseEntity.ok(page);
    }

    //관리자의 유저 상세 보기
    @GetMapping("/{id}")
    public ResponseEntity<UserMyPageResponseDTO> getOneUser(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminUserService.getUser(id));
    }

    //관리자가 직접 유저 계정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    //todo 관리자 컨트롤러에 조치 컨트롤러 추가. 신고 서비스 받아내서 사용.
    //-> 신고 DTO 신고 서비스 메서드 이름 알면.
}
