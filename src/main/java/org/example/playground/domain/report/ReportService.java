package org.example.playground.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    
    //유저 -> 신고 생성
    ReportResponseDTO reportUser(ReportCreateRequestDTO reportCreateRequestDTO);

    //관리자 -> 신고 승인 메소드
    void approve(Long reportId);

    //관리자 -> 신고 거부 메소드
    void reject(Long reportId);

    //관리자 -> 대기 중인 신고 목록 보여주기
    Page<ReportResponseDTO> getPendingReports(Pageable pageable);

}
