package org.example.playground.domain.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReportReason {

    @Enumerated(EnumType.STRING)
    @Column(name = "category",nullable=false)
    private ReportCategory reportCategory;

    @Column(name = "detail", nullable = true)
    private String detail;

    public ReportReason(ReportCategory reportCategory, String detail) {
        this.reportCategory = reportCategory;
        this.detail = detail;
    }


}
