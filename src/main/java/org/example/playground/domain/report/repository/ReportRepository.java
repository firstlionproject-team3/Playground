package org.example.playground.domain.report.repository;

import org.example.playground.domain.report.ReportResponseDTO;
import org.example.playground.domain.report.entity.Report;
import org.example.playground.domain.report.entity.ReportStatus;
import org.example.playground.domain.report.entity.ReportTarget;
import org.example.playground.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    boolean existsReportByReporter_IdAndReported_Id(Long reporterId, Long reportedId);
    Page<Report> findAllByStatus(ReportStatus status, Pageable pageable);

    boolean existsByReporterAndReportedAndTarget(User reporter, User reported, ReportTarget target);
}
