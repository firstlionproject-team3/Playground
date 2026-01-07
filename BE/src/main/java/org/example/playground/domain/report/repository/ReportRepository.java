package org.example.playground.domain.report.repository;

import jakarta.persistence.LockModeType;
import org.example.playground.domain.report.entity.Report;
import org.example.playground.domain.report.entity.ReportStatus;
import org.example.playground.domain.report.entity.ReportTarget;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    boolean existsReportByReporter_IdAndReported_Id(Long reporterId, Long reportedId);
    Page<Report> findAllByStatus(ReportStatus status, Pageable pageable);

    boolean existsByReporterAndReportedAndTarget(User reporter, User reported, ReportTarget target);

    @Query("""
    SELECT r FROM Report r
    JOIN r.reporter reporter
    JOIN r.reported reported
    WHERE r.status = :status
    AND reporter.status = org.example.playground.domain.user.entity.UserStatus.ACTIVE
    AND reported.status = org.example.playground.domain.user.entity.UserStatus.ACTIVE
    """)
    Page<Report> findAllByStatusExcludingDeletedUsers(
            @Param("status") ReportStatus status,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Report r where r.id = :id")
    Optional<Report> findUserByIdForUpdate(Long id);
}
