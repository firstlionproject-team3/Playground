package org.example.playground.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.report.ReportResponseDTO;
import org.example.playground.domain.report.ReportService;
import org.example.playground.domain.report.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    //대기 신고 목록 조회
    @GetMapping
    public ResponseEntity<Page<ReportResponseDTO>> getReports(Pageable pageable) {
        return ResponseEntity.ok(reportService.getPendingReports(pageable));
    }

    //승인
    @PostMapping("/{reportId}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long reportId) {
        reportService.approve(reportId);
        return ResponseEntity.noContent().build();
    }

    //거부
    @PostMapping("/{reportId}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long reportId) {
        reportService.reject(reportId);
        return ResponseEntity.noContent().build();
    }
}
