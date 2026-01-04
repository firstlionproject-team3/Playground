package org.example.playground.domain.report.entity;

import lombok.Getter;

@Getter
public enum ReportCategory {
    SPAM("스팸/광고"),
    ABUSE("욕설/비방"),
    INAPPROPRIATE("부적절한 내용"),
    COPYRIGHT("저작권 침해"),
    MISINFORMATION("허위 정보"),
    ETC("기타");

    private final String description;

    ReportCategory(String description) {
        this.description = description;
    }
}
