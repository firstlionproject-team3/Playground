package org.example.playground.domain.report;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.playground.domain.report.entity.EntityType;
import org.example.playground.domain.report.entity.Report;
import org.example.playground.domain.report.entity.ReportCategory;
import org.example.playground.domain.report.entity.ReportStatus;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ReportResponseDTO {

    private Long reportId;
    private String reporterNickname;
    private String reportedNickname;
    private EntityType entityType;
    private Long entityId;
    private ReportCategory reportCategory;
    private String detail;
    private ReportStatus reportStatus;
    private LocalDateTime reportDate;


    public static ReportResponseDTO from(Report report) {
        return ReportResponseDTO.builder()
                .reportId(report.getId())
                .reporterNickname(report.getReporter().getNickname())
                .reportedNickname(report.getReported().getNickname())
                .entityType(report.getTarget().getEntityType())
                .entityId(report.getTarget().getEntityId())
                .reportCategory(report.getReason().getReportCategory())
                .detail(report.getReason().getDetail())
                .reportStatus(report.getStatus())
                .reportDate(report.getCreatedAt())
                .build();

    }
}
