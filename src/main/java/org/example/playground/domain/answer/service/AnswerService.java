package org.example.playground.domain.answer.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.dto.request.AnswerCreateRequestDTO;
import org.example.playground.domain.answer.dto.request.AnswerUpdateRequestDTO;
import org.example.playground.domain.answer.dto.response.AnswerDetailResponseDTO;
import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.answer.exception.AnswerNotFoundException;
import org.example.playground.domain.answer.exception.CannotAnswerOwnQuestionException;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.service.NotificationService;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.question.exception.QuestionNotFoundException;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 답변 등록
    @Transactional
    public AnswerDetailResponseDTO create(Long questionId, Long userId, AnswerCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userID=" + userId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        // 자기 질문에 답변 달기 금지
        if (userId.equals(question.getUser().getId())) {
            throw new CannotAnswerOwnQuestionException();
        }

        Answer answer = Answer.create(question, user, request.content());
        Answer saved = answerRepository.save(answer);

        // 답변 등록 시 질문 작성자에게 알림
        notifyNewAnswer(saved);

        return AnswerDetailResponseDTO.from(saved);
    }

    // 해당 질문의 답변 조회 - 전체
    @Transactional(readOnly = true)
    public Page<AnswerSummaryResponseDTO> getAnswerByQuestion(Long questionId, Pageable pageable) {
        return answerRepository
                .findByQuestion_IdOrderByAcceptedDescCreatedAtDesc(questionId, pageable)
                .map(AnswerSummaryResponseDTO::from);
    }

    // 답변 수정 - 작성자만
    @Transactional
    public AnswerDetailResponseDTO update(Long answerId, Long userId, AnswerUpdateRequestDTO request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        validateOwner(answer, userId);
        answer.update(request.content());

        return AnswerDetailResponseDTO.from(answer);
    }

    // 답변 삭제 - 작성자만
    @Transactional
    public void delete(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        validateOwner(answer, userId);
        answerRepository.delete(answer);
    }

    // 답변 단건 조회 (알림 클릭 시 사용)
    @Transactional(readOnly = true)
    public AnswerDetailResponseDTO findOne(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));
        return AnswerDetailResponseDTO.from(answer);
    }

    // ===================== private helpers =====================

    // 답변 작성자인지 검증
    private void validateOwner(Answer answer, Long userId) {
        Long ownerId = answer.getUser().getId();
        if (!ownerId.equals(userId)) {
            throw new RuntimeException("작성자만 가능합니다.");
        }
    }

    // 답변 등록 알림 전송
    private void notifyNewAnswer(Answer saved) {
        Long receiverId = saved.getQuestion().getUser().getId(); // 질문 작성자
        Long senderId = saved.getUser().getId();                 // 답변 작성자

        // 자기 자신에게 알림 X (방어적 정책)
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

    // NEW_ANSWER 알림 content 생성 (임시 텍스트)
    private String buildNewAnswerContent(Answer saved) {
        return "내 질문(" + saved.getQuestion().getId()
                + ")에 답변이 달렸습니다. answerId=" + saved.getId();
    }
}
