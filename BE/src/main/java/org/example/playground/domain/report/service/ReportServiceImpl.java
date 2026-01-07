package org.example.playground.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.domain.answer.service.AnswerService;
import org.example.playground.domain.comment.service.CommentService;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.service.NotificationService;
import org.example.playground.domain.question.service.QuestionService;
import org.example.playground.domain.report.dto.ReportCreateRequestDTO;
import org.example.playground.domain.report.dto.ReportResponseDTO;
import org.example.playground.domain.report.entity.*;
import org.example.playground.domain.report.exception.ReportErrorCode;
import org.example.playground.domain.report.exception.ReportException;
import org.example.playground.domain.report.repository.ReportRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final NotificationService notificationService;


    @Override
    public ReportResponseDTO reportUser(Long reporterId, ReportCreateRequestDTO dto) {

        //1. 신고 대상 확인
        validateEntityExists(dto.getEntityType(), dto.getEntityId());

        //2. user 조회
        log.info("Validating entity of {}", dto.getEntityType());

        User reporter = userService.findUserOrThrow(reporterId);
        User reported = userService.findUserOrThrow(dto.getReportedId());
        if(reported.isDeleted()){
            throw new ReportException(ReportErrorCode.REPORTED_USER_DELETED);
        }

        //3. 자기 자신 체크 확인
        log.info("checking self report... ");
        if(reporter.getId() == reported.getId()) {
            throw new ReportException(ReportErrorCode.SELF_REPORT_NOT_ALLOWED);
        }

        //4. 중복 신고 체크
        log.info("already reported");
        EntityType entityType = dto.getEntityType();
        Long entityId = dto.getEntityId();
        ReportTarget target = new ReportTarget(entityId, entityType);
        if (existsReport(reporter, reported, target)) {
            throw new ReportException(ReportErrorCode.DUPLICATE_REPORT);
        }

        ReportCategory category = dto.getCategory();
        String reasonDetail = dto.getReasonDetail();
        ReportReason reason = new ReportReason(category, reasonDetail);

        Report report = Report.create(reporter, reported, target, reason);
        log.info("creating report");
        reportRepository.save(report);

        //5. 관리자에게 알림 전송
        log.info("send report notification...");


        NotificationType notificationType = NotificationType.REPORT_RECEIVED;
        String msg = "[신고]" + reporter.getNickname() + " 요청으로 신고가 발생했습니다.";
        notificationService.sendToAllAdmins(notificationType, reporter, msg);

        return ReportResponseDTO.from(report);
    }

    @Override
    public void approve(Long reportId) {

        Report report = reportRepository.findUserByIdForUpdate(reportId).orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));

        //대기 상태가 아니라면 승인 불가
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ReportException(ReportErrorCode.REPORT_NOT_PENDING);
        }

        report.approve();

        // 2. 신고된 콘텐츠 삭제/숨김 처리
        deleteReportedContent(report.getTarget());

        // 3. 피신고자 처벌
        punishReportedUser(report.getReported());

    }

    @Override
    public void reject(Long reportId) {

        Report report = reportRepository.findUserByIdForUpdate(reportId).orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));

        //대기 상태가 아니라면 거부 불가
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ReportException(ReportErrorCode.REPORT_NOT_PENDING);
        }

        report.reject();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponseDTO> getPendingReports(Pageable pageable) {
        return reportRepository.findAllByStatusExcludingDeletedUsers(ReportStatus.PENDING, pageable)
                .map(ReportResponseDTO::from);
    }

    private void validateEntityExists(EntityType entityType, Long entityId) {

        //검증 실패 시 예외 발생
        switch (entityType) {
            case QUESTION:
                questionService.validateQuestionExists(entityId);
                break;
            case ANSWER:
                answerService.validateAnswerExists(entityId);
                break;
            case COMMENT:
                commentService.existsByCommentId(entityId);
                break;
            default:
                throw new ReportException(ReportErrorCode.INVALID_REPORT_TARGET_TYPE);
        }

    }

    //이미 신고된 안건인지 확인.
    private boolean existsReport(User reporter, User reported, ReportTarget target) {

        return reportRepository.existsByReporterAndReportedAndTarget(
                reporter, reported, target
        );
    }

    private void deleteReportedContent(ReportTarget target) {
        switch (target.getEntityType()) {
            case QUESTION:
                //질문 데이터 삭제
                questionService.deleteQuestionByAdmin(target.getEntityId());
                break;

            case ANSWER:
                //소프트 삭제, 관리자에 의해 삭제된 답변입니다. 로 내용 교체
                answerService.softDeleteAnswerByAdmin(target.getEntityId());
                break;

            case COMMENT:
                //댓글 삭제, 관리자에 의해 삭제된 댓글입니다. 로 내용 교체
                commentService.softDeleteComment(target.getEntityId());
                break;
        }
    }

    private void punishReportedUser(User user) {

        log.info("punished userId = {}", user.getId());
        userService.deleteUser(user.getId());
    }

}