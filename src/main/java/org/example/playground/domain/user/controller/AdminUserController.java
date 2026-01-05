package org.example.playground.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.user.dto.ForAdminDTO;
import org.example.playground.domain.user.dto.UserMyPageResponseDTO;
import org.example.playground.domain.user.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    //관리자의 유저 목록 보기
    @GetMapping
    public ResponseEntity<Page<ForAdminDTO>> getUserListByAdmin(Pageable pageable) {
        Page<ForAdminDTO> page = adminUserService.getAllUsersByAdmin(pageable)
                .map(ForAdminDTO::forAdminDTOFromEntity);
        return ResponseEntity.ok(page);
    }

    //관리자의 유저 상세 보기
    @GetMapping("/{id}")
    public ResponseEntity<UserMyPageResponseDTO> getOneUserByAdmin(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminUserService.getUserByAdmin(id));
    }

    //관리자가 직접 유저 계정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable("id") Long id){
        adminUserService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
