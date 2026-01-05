package org.example.playground.domain.report.entity;

import lombok.Getter;

@Getter
public enum ReportStatus {

    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거부");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }
}
