package org.example.playground.domain.question.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.service.NotificationService;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionDetailResponseDTO;
import org.example.playground.domain.question.dto.response.QuestionSummaryResponseDTO;
import org.example.playground.domain.question.exception.QuestionNotFoundException;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    //질문 생성
    @Transactional
    public QuestionDetailResponseDTO create(Long userId, QuestionCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userId=" + userId));
        System.out.println("questionservice log");
        Question question = Question.create(user, request.title(), request.content());
        Question saved = questionRepository.save(question);

        return QuestionDetailResponseDTO.from(saved);

    }

    //질문 전체목록 보기
    @Transactional(readOnly = true)
    public Page<QuestionSummaryResponseDTO> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(QuestionSummaryResponseDTO::from);
    }


    //질문 검색
    @Transactional(readOnly = true)
    public Page<QuestionSummaryResponseDTO> search(String type, String keyword, Pageable pageable) {
        //keyword가 없으면 전체질문목록 보여주기
        if(keyword == null || keyword.isBlank()) {
            return getQuestions(pageable);
        }

        //type이 null이면
        String t;
        if(type == null) t = "all";
        else t = type.trim().toLowerCase();

        //type에 따라 repository 메서드 선택
        Page<Question> page;
        switch(t) {
            case "title":
                page =  questionRepository.findByTitleContainingIgnoreCase(keyword, pageable);
                break;
            case "content":
                page =  questionRepository.findByContentContainingIgnoreCase(keyword, pageable);
                break;

            case "all":
            default:
                page = questionRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
                break;
        }
        //엔티티를 DTO로 변환 - 도움
        return page.map(QuestionSummaryResponseDTO::from);
    }


    //로그인한 사용자가 자기가 작성한 질문 목록을 조회
    @Transactional(readOnly = true)
    public Page<QuestionSummaryResponseDTO> getMyQuestions(Long userId, Pageable pageable) {
        //최신순
        Page<Question> page = questionRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        return page.map(QuestionSummaryResponseDTO::from);
    }
    


    //질문 1건 상세 조회
    @Transactional
    public QuestionDetailResponseDTO findOne(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        //조회수 증가++, 조회수는 변경된느 작업이므로 (readOnly = true) 사용 X
        question.increaseViewCount();

        return QuestionDetailResponseDTO.from(question);
    }

    //질문 수정
    //권한 체크
    @Transactional
    public QuestionDetailResponseDTO update(Long id, Long userId, QuestionUpdateRequestDTO request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));

        validateOwner(question, userId);
        question.update(request.title(), request.content());
        return QuestionDetailResponseDTO.from(question);
    }

    //질문 삭제
    @Transactional
    public void delete(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        validateOwner(question, userId);

        questionRepository.delete(question);
    }

    //작성자 검증
    private void validateOwner(Question question, Long userId) {
        Long ownerId = question.getUser().getId();
        if (!ownerId.equals(userId)) {
            //프로젝트 공통 예외로?
            throw new RuntimeException("작성자만 가능합니다.");
        }
    }

    //질문 신고
    @Transactional
    public void report(Long questionId, Long reporterId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        //신고 처리 (도메인 상태 변경)
        //reportBy() : 질문이 신고된 횟수를 기록한 메서드
        question.reportBy(reporterId);
        //TODO 신고 정책/중복신고/횟수 누적 등은 나중에

        Long adminId = 1L; //임시 관리자 계정 ID (팀에서 확정 필요)

        // 알림 전송 (현재 NotificationService는 REPORT_RECEIVED만 지원)
        NotificationRequestDTO req = new NotificationRequestDTO(
                adminId,
                reporterId,
                NotificationType.REPORT_RECEIVED,
                "질문 신고가 접수되었습니다. questionId=" + questionId
        );
        notificationService.createNotification(req);
    }

}
