package org.example.playground.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.example.playground.domain.report.entity.EntityType;
import org.example.playground.domain.report.entity.ReportCategory;

@Getter
public class ReportCreateRequestDTO {

    @NotNull
    @Positive
    private Long reportedId; //신고당한 유저 id

    @NotNull
    private EntityType entityType;

    @NotNull @Positive
    private Long entityId;

    @NotNull
    private ReportCategory category;

    @Size(max = 500)
    private String reasonDetail; //null 허용

}
