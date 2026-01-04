package org.example.playground.domain.report;

import lombok.Getter;
import org.example.playground.domain.report.entity.EntityType;
import org.example.playground.domain.report.entity.ReportCategory;

@Getter
public class ReportCreateRequestDTO {

    private Long reporterId; //신고한 유저 id
    private Long reportedId; //신고당한 유저 id
    private EntityType entityType;
    private Long entityId;
    private ReportCategory category;
    private String reasonDetail;

}
