package org.example.playground.domain.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.playground.domain.report.service.ReportService;
import org.example.playground.domain.report.dto.ReportCreateRequestDTO;
import org.example.playground.domain.report.dto.ReportResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;


    /**
     * 신고 접수 (USER 이상 권한 필요)
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(@Valid @RequestBody ReportCreateRequestDTO reportCreateRequestDTO) {

        ReportResponseDTO reportResponseDTO = reportService.reportUser(reportCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportResponseDTO);
    }

    /**
     * 대기 중인 신고 목록 조회 (ADMIN 권한 필요)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ReportResponseDTO>> getReports(@PageableDefault(size = 10) Pageable pageable) {

        Page<ReportResponseDTO> pendingReports = reportService.getPendingReports(pageable);
        return ResponseEntity.ok(pendingReports);
    }

    /**
     * 신고 승인 - 신고된 콘텐츠 삭제 및 사용자 처벌 (ADMIN 권한 필요)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<Void> approveReport(@PathVariable("reportId") Long reportId) {
        reportService.approve(reportId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 신고 거부 (ADMIN 권한 필요)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<Void> rejectReport(@PathVariable("reportId") Long reportId) {
        reportService.reject(reportId);
        return ResponseEntity.noContent().build();
    }
}
