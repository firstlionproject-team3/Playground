package org.example.playground.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "reports",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "reporter_user_id",
                        "reported_user_id",
                        "entity_type",
                        "entity_id"
                }
                )
        }
)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reported;

    @Embedded
    private ReportTarget target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Embedded
    private ReportReason reason;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Report create(User reporter, User reported, ReportTarget target, ReportReason reason) {
        return Report.builder()
                .reporter(reporter)
                .reported(reported)
                .target(target)
                .status(ReportStatus.PENDING)
                .reason(reason)
                .build();
    }


    public void approve() {
        this.status = ReportStatus.APPROVED;
    }

    public void reject() {
        this.status = ReportStatus.REJECTED;
    }

    public void pending() {
        this.status = ReportStatus.PENDING;
    }

}
