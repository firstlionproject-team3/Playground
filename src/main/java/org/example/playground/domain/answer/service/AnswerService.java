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

    //답변 등록
    @Transactional
    public AnswerDetailResponseDTO create(Long questionId, Long userId, AnswerCreateRequestDTO request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userID=" + userId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        //자기 질문에 답변 달기 금지
        Long questionOwnerId = question.getUser().getId();
        if (userId.equals(question.getUser().getId())) {
            throw new CannotAnswerOwnQuestionException();
        }

        Answer answer = Answer.create(question, user, request.content());
        Answer saved = answerRepository.save(answer);

        //todo 답변 등록 질문 작성자에게 알림

        return AnswerDetailResponseDTO.from(saved);

    }

    //해당 질문 답변 조회 - 전체
    @Transactional(readOnly = true)
    public Page<AnswerSummaryResponseDTO> getAnswerByQuestion(Long questionId, Pageable pageable){
        return answerRepository
                .findByQuestion_IdOrderByAcceptedDescCreatedAtDesc(questionId, pageable)
                .map(AnswerSummaryResponseDTO::from);
    }



    //답변 수정 - 작성자만
    @Transactional
    public AnswerDetailResponseDTO update(Long answerId, Long userId, AnswerUpdateRequestDTO request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        validateOwner(answer, userId);
        answer.update(request.content());

        return AnswerDetailResponseDTO.from(answer);
    }

    //답변 삭제 (작성자만)
    @Transactional
    public void delete(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        validateOwner(answer, userId);
        answerRepository.delete(answer);
    }

    //로그인한 사용자가 작성자인지 검증
    private void validateOwner(Answer answer, Long userId) {
        Long ownerId = answer.getUser().getId();
        if (!ownerId.equals(userId)) {
            throw new RuntimeException("작성자만 가능합니다.");
        }
    }

    //답변등록알림 클릭 시 답변 상세 조회용
    @Transactional(readOnly = true)
    public AnswerDetailResponseDTO findOne(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));
        return AnswerDetailResponseDTO.from(answer);
    }
}
