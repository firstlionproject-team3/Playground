package org.example.playground.domain.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.playground.domain.report.entity.*;
import org.example.playground.domain.report.repository.ReportRepository;
import org.example.playground.domain.user.entity.User;
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
/*    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    */

/*    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;*/
    //private final CommentService commentRepository;


    @Override
    public Report reportUser(ReportCreateRequestDTO dto) {

        //1. 신고 대상 확인
        validateEntityExists(dto.getEntityType(), dto.getEntityId());

        //2. user 조회
        // userService.getUser(dto.getReporterId()); -->???????
        //todo: 교체해야함
        /*User reporter = userRepository.findById(dto.getReporterId()).orElseThrow(() -> new UserNotFoundException("..."));
        User reported = userRepository.findById(dto.getReportedId()).orElseThrow(() -> new UserNotFoundException("..."));*/

        User reporter = null;
        User reported = null;
        //3. 자기 자신 체크 확인
        if (reporter.equals(reported)) {
            throw new RuntimeException("...");
        }

        //4. 중복 신고 체크
        EntityType entityType = dto.getEntityType();
        Long entityId = dto.getEntityId();
        ReportTarget target = new ReportTarget(entityId, entityType);
        if (existsReport(reporter, reported, target)) {
            throw new RuntimeException("Report already exists");
        }

        ReportCategory category = dto.getCategory();
        String reasonDetail = dto.getReasonDetail();
        ReportReason reason = new ReportReason(category, reasonDetail);

        Report report = Report.create(reporter, reported, target, reason);
        return reportRepository.save(report);
    }

    @Override
    public void approve(Long reportId) {

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("report not found"));

        //일단 대기 상태가 아니라면 승인 불가
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("report status is not PENDING");
        }

        report.approve();

        // 2. 신고된 콘텐츠 삭제/숨김 처리
        deleteReportedContent(report.getTarget());

        // 3. 피신고자 처벌
        punishReportedUser(report.getReported());

    }

    @Override
    public void reject(Long reportId) {

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("report not found"));

        //일단 대기 상태가 아니라면 거부 불가
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("report status is not PENDING");
        }

        report.reject();
    }

    @Override
    public Page<ReportResponseDTO> getPendingReports(Pageable pageable) {

        return reportRepository.findAllByStatus(ReportStatus.PENDING, pageable)
                .map(ReportResponseDTO::from);
    }

    private void validateEntityExists(EntityType entityType, Long entityId) {

        switch (entityType) {
            case QUESTION:
                //todo: 교체해야함
/*                if (!questionRepository.existsById(entityId)) {
                    throw new IllegalArgumentException("해당 질문이 존재하지 않습니다");
                }*/
                break;
            case ANSWER:
                //todo: 교체해야함
/*                if (!answerRepository.existsById(entityId)) {
                    throw new IllegalArgumentException("해당 답변이 존재하지 않습니다");
                }*/
                break;
/*            case COMMENT:
                if (!commentRepository.existsById(entityId)) {
                    throw new IllegalArgumentException("해당 댓글이 존재하지 않습니다");
                }
                break;*/
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

                //해당 질문 처리
                //질문이 신고당했을 때 -> 질문 삭제
                break;

            case ANSWER:
                //todo: 교체해야함
/*                Answer answer = answerRepository.findById(target.getEntityId())
                        .orElseThrow(() -> new AnswerNotFoundException(target.getEntityId()));*/
                //해당 답변 처리
                //답변이 신고 당했을 때 -> 관리자에 의해 삭제된 답변입니다.
                break;

            case COMMENT:
                // Comment comment = commentRepository.findById(target.getEntityId())...
                // comment.delete();
                //댓글이 신고 당했을 때 ->  관리자에 의해 삭제된 댓글 -> 댓글 삭제
                break;
        }
    }

    private void punishReportedUser(User user) {

        log.info("진짜 처벌됨 userId = {}",user.getId());
        //userService.deleteUser(user.getId());
        //userRepository.delete(user);
    }

}