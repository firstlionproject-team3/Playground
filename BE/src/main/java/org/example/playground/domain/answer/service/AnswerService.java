package org.example.playground.domain.answer.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.dto.request.AnswerCreateRequestDTO;
import org.example.playground.domain.answer.dto.request.AnswerUpdateRequestDTO;
import org.example.playground.domain.answer.dto.response.AnswerDetailResponseDTO;
import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.answer.exception.AnswerErrorCode;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.service.NotificationService;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.question.exception.QuestionErrorCode;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;
import org.example.playground.domain.reaction.service.ReactionService;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReactionService reactionService;

    // 답변 등록
    @Transactional
    public AnswerDetailResponseDTO create(Long questionId, Long userId, AnswerCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userID=" + userId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
                        )
                );

        // 자기 질문에 답변 달기 금지
        if (userId.equals(question.getUser().getId())) {
            throw new BusinessException(AnswerErrorCode.CANNOT_ANSWER_OWN_QUESTION);
        }

        Answer answer = Answer.create(question, user, request.content());
        Answer saved = answerRepository.save(answer);

        // 답변 등록 시 질문 작성자에게 알림
        notifyNewAnswer(saved);

        return AnswerDetailResponseDTO.from(saved);
    }

    // 해당 질문의 답변 조회 - 전체
    @Transactional(readOnly = true)
    public Page<AnswerSummaryResponseDTO> getAnswerByQuestion(Long questionId, Long userId, Pageable pageable) {

        Page<Answer> page = answerRepository
                .findByQuestion_IdOrderByAcceptedDescCreatedAtDesc(questionId, pageable);

        List<Answer> answers = page.getContent();
        List<Long> ids = answers.stream().map(Answer::getId).toList();

        if (ids.isEmpty()) {
            return page.map(a -> AnswerSummaryResponseDTO.from(a, 0L, 0L, "NONE"));
        }

        Map<Long, Long> likeMap = reactionService.getLikeCountMap(TargetType.ANSWER, ids);
        Map<Long, Long> dislikeMap = reactionService.getDislikeCountMap(TargetType.ANSWER, ids);

        // ✅ 핵심: 람다에서 쓰려면 final/effectively final 이어야 함
        final Map<Long, String> myMap =
                (userId == null)
                        ? Map.of()
                        : reactionService.getMyReactionMap(userId, TargetType.ANSWER, ids)
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().name()
                        ));

        List<AnswerSummaryResponseDTO> dtoList = answers.stream()
                .map(a -> AnswerSummaryResponseDTO.from(
                        a,
                        likeMap.getOrDefault(a.getId(), 0L),
                        dislikeMap.getOrDefault(a.getId(), 0L),
                        myMap.getOrDefault(a.getId(), "NONE")
                ))
                .toList();

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // 내 답변 목록 보기 - 마이페이지
    @Transactional(readOnly = true)
    public Page<AnswerSummaryResponseDTO> getMyAnswers(Long userId, Pageable pageable) {

        Page<Answer> page = answerRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

        List<Long> ids = page.getContent().stream().map(Answer::getId).toList();
        if (ids.isEmpty()) {
            return page.map(a -> AnswerSummaryResponseDTO.from(a, 0L, 0L, "NONE"));
        }

        Map<Long, Long> likeMap = reactionService.getLikeCountMap(TargetType.ANSWER, ids);
        Map<Long, Long> dislikeMap = reactionService.getDislikeCountMap(TargetType.ANSWER, ids);

        // ✅ 여기도 동일하게 final로
        final Map<Long, String> myMap =
                (userId == null)
                        ? Map.of()
                        : reactionService.getMyReactionMap(userId, TargetType.ANSWER, ids)
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().name()
                        ));

        List<AnswerSummaryResponseDTO> dtoList = page.getContent().stream()
                .map(a -> AnswerSummaryResponseDTO.from(
                        a,
                        likeMap.getOrDefault(a.getId(), 0L),
                        dislikeMap.getOrDefault(a.getId(), 0L),
                        myMap.getOrDefault(a.getId(), "NONE")
                ))
                .toList();

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // 답변 수정 - 작성자만
    @Transactional
    public AnswerDetailResponseDTO update(Long answerId, Long userId, AnswerUpdateRequestDTO request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() ->
                        new BusinessException(
                                AnswerErrorCode.ANSWER_NOT_FOUND,
                                "answerId=" + answerId
                        )
                );

        validateOwner(answer, userId);
        answer.update(request.content());

        return AnswerDetailResponseDTO.from(answer);
    }

    // 답변 삭제 - 작성자만
    @Transactional
    public void delete(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() ->
                        new BusinessException(
                                AnswerErrorCode.ANSWER_NOT_FOUND,
                                "answerId=" + answerId
                        )
                );

        validateOwner(answer, userId);
        answerRepository.delete(answer);
    }

    // 답변 단건 조회 (알림 클릭 시 사용) - reaction 포함
    @Transactional(readOnly = true)
    public AnswerDetailResponseDTO findOne(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(
                        AnswerErrorCode.ANSWER_NOT_FOUND,
                        "answerId=" + answerId
                ));

        long likeCount = reactionService.getLikeCountMap(TargetType.ANSWER, List.of(answerId))
                .getOrDefault(answerId, 0L);

        long dislikeCount = reactionService.getDislikeCountMap(TargetType.ANSWER, List.of(answerId))
                .getOrDefault(answerId, 0L);

        String myReactionType = "NONE";
        if (userId != null) {
            ReactionType my = reactionService
                    .getMyReactionMap(userId, TargetType.ANSWER, List.of(answerId))
                    .get(answerId);

            if (my != null) {
                myReactionType = my.name(); // LIKE / DISLIKE
            }
        }

        return AnswerDetailResponseDTO.from(answer, likeCount, dislikeCount, myReactionType);
    }

    // 답변 존재 여부만 확인
    @Transactional(readOnly = true)
    public void validateAnswerExists(Long answerId) {
        if (!answerRepository.existsById(answerId)) {
            throw new BusinessException(
                    AnswerErrorCode.ANSWER_NOT_FOUND,
                    "answerId=" + answerId
            );
        }
    }

    // 관리자에 의해 답변삭제 (soft)
    @Transactional
    public void softDeleteAnswerByAdmin(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() ->
                        new BusinessException(
                                AnswerErrorCode.ANSWER_NOT_FOUND,
                                "answerId=" + answerId
                        )
                );

        answer.softDeleteByAdmin();
    }

    @Transactional
    public void report(Long questionId, Long answerId, Long reporterId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() ->
                        new BusinessException(
                                AnswerErrorCode.ANSWER_NOT_FOUND,
                                "해당하는 답변을 찾을 수 없습니다. answerId = " + answerId
                        )
                );

        Long actualQuestionId = answer.getQuestion().getId();
        if (!actualQuestionId.equals(questionId)) {
            throw new BusinessException(
                    AnswerErrorCode.ANSWER_NOT_IN_QUESTION,
                    "답변이 해당 질문에 속하지 않습니다. questionId=" + questionId + ", answerId=" + answerId
            );
        }

        answer.reportBy(reporterId);

        Long adminId = 5L; // 임시

        NotificationRequestDTO req = new NotificationRequestDTO(
                adminId,
                reporterId,
                NotificationType.REPORT_RECEIVED,
                "답변 신고가 접수되었습니다. questionId=" + questionId + ", answerId=" + answerId
        );
        notificationService.createNotification(req);
    }

    // ===================== private helpers =====================

    private void validateOwner(Answer answer, Long userId) {
        Long ownerId = answer.getUser().getId();
        if (!ownerId.equals(userId)) {
            throw new BusinessException(AnswerErrorCode.ANSWER_OWNER_MISMATCH);
        }
    }

    private void notifyNewAnswer(Answer saved) {
        Long receiverId = saved.getQuestion().getUser().getId();
        Long senderId = saved.getUser().getId();

        if (receiverId.equals(senderId)) {
            return;
        }

        notificationService.createNotification(
                new NotificationRequestDTO(
                        receiverId,
                        senderId,
                        NotificationType.NEW_ANSWER,
                        buildNewAnswerContent(saved)
                )
        );
    }

    private String buildNewAnswerContent(Answer saved) {
        return "내 질문(" + saved.getQuestion().getId()
                + ")에 답변이 달렸습니다. answerId=" + saved.getId();
    }
}
