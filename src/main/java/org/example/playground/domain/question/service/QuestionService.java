package org.example.playground.domain.question.service;

import lombok.RequiredArgsConstructor;
import org.example.playground.domain.answer.dto.response.AnswerSummaryResponseDTO;
import org.example.playground.domain.answer.entity.Answer;
import org.example.playground.domain.answer.exception.AnswerErrorCode;
import org.example.playground.domain.answer.repository.AnswerRepository;
import org.example.playground.domain.notification.dto.NotificationRequestDTO;
import org.example.playground.domain.notification.entity.NotificationType;
import org.example.playground.domain.notification.service.NotificationService;
import org.example.playground.domain.question.dto.request.QuestionUpdateRequestDTO;
import org.example.playground.domain.question.dto.response.QuestionDetailResponseDTO;
import org.example.playground.domain.question.dto.response.QuestionSummaryResponseDTO;
import org.example.playground.domain.question.exception.*;
import org.example.playground.domain.reaction.entity.ReactionType;
import org.example.playground.domain.reaction.entity.TargetType;
import org.example.playground.domain.reaction.service.ReactionService;
import org.example.playground.domain.user.entity.User;
import org.example.playground.domain.user.exception.UserNotFoundException;
import org.example.playground.domain.user.repository.UserRepository;
import org.example.playground.global.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.example.playground.domain.question.dto.request.QuestionCreateRequestDTO;
import org.example.playground.domain.question.entity.Question;
import org.example.playground.domain.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AnswerRepository answerRepository;
    private final ReactionService reactionService;

    //질문 생성
    @Transactional
    public Long create(Long userId, QuestionCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userId=" + userId));

        Question question = Question.create(user, request.title(), request.content());
        Question saved = questionRepository.save(question);

        return saved.getId();
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



    // 질문 1건 상세 조회
    @Transactional
    public QuestionDetailResponseDTO findOne(Long questionId, Long userId) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
                ));

        // 조회수 증가
        question.increaseViewCount();

        // 답변 목록(채택 우선, 최신순)
        List<Answer> answers = answerRepository
                .findByQuestion_IdOrderByAcceptedDescCreatedAtDesc(questionId);

        // 1) 질문 리액션
        long qLike = reactionService.getLikeCount(TargetType.QUESTION, questionId);
        long qDislike = reactionService.getDislikeCount(TargetType.QUESTION, questionId);
        String qMyReaction = reactionService.getUserReactionType(userId, TargetType.QUESTION, questionId)
                .map(ReactionType::name)
                .orElse("NONE");

        // 2) 답변 리액션 (N+1 방지)
        List<Long> answerIds = answers.stream().map(Answer::getId).toList();

        Map<Long, Long> likeMap = reactionService.getLikeCountMap(TargetType.ANSWER, answerIds);
        Map<Long, Long> dislikeMap = reactionService.getDislikeCountMap(TargetType.ANSWER, answerIds);

        // ReactionService.getMyReactionMap은 Map<Long, ReactionType> 반환이니까 String으로 변환
        Map<Long, String> myMap = reactionService.getMyReactionMap(userId, TargetType.ANSWER, answerIds)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().name()
                ));

        // 3) 답변 DTO 생성
        List<AnswerSummaryResponseDTO> answerDtos = answers.stream()
                .map(a -> AnswerSummaryResponseDTO.from(
                        a,
                        likeMap.getOrDefault(a.getId(), 0L),
                        dislikeMap.getOrDefault(a.getId(), 0L),
                        myMap.getOrDefault(a.getId(), "NONE")
                ))
                .toList();

        // 4) 최종 응답
        return QuestionDetailResponseDTO.from(
                question,
                qLike,
                qDislike,
                qMyReaction,
                answerDtos
        );
    }

    //질문 수정
    //권한 체크

    @Transactional
    public void update(Long id, Long userId, QuestionUpdateRequestDTO request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "해당하는 질문을 찾을 수 없습니다. questionId = " + id
                        )
                );

        validateOwner(question, userId);
        question.update(request.title(), request.content());
    }

    //질문 삭제
    @Transactional
    public void delete(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
                        )
                );

        validateOwner(question, userId);

        questionRepository.delete(question);
    }

    //작성자 검증
    private void validateOwner(Question question, Long userId) {
        Long ownerId = question.getUser().getId();
        if (!ownerId.equals(userId)) {
            throw new BusinessException(QuestionErrorCode.QUESTION_OWNER_MISMATCH);
        }
    }

    //질문 존재 여부만 확인
    @Transactional(readOnly = true)
    public void validateQuestionExists(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_NOT_FOUND,
                    "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
            );

        }
    }

    //관리자에 의해 질문 삭제(hard)
    @Transactional
    public void deleteQuestionByAdmin(Long questionId) {
        // 존재 확인(또는 findOrThrow로 엔티티 가져오기)
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
                        )
                );


        questionRepository.delete(question);
    }


    //질문 신고
    @Transactional
    public void report(Long questionId, Long reporterId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
                        )
                );


        //신고 처리 (도메인 상태 변경)
        //reportBy() : 질문이 신고된 횟수를 기록한 메서드
        question.reportBy(reporterId);

        //5L = db에 박아놓은 admin id
        Long adminId = 5L; //임시 관리자 계정 ID, 민섭님한테 관리자계정 찾는 로직 부탁하기

        // 알림 전송 (현재 NotificationService는 REPORT_RECEIVED만 지원)
        NotificationRequestDTO req = new NotificationRequestDTO(
                adminId,
                reporterId,
                NotificationType.REPORT_RECEIVED,
                "질문 신고가 접수되었습니다. questionId=" + questionId
        );
        notificationService.createNotification(req);
    }

    //답변 채택
    @Transactional
    public void acceptAnswer(Long questionId, Long answerId, Long userId) {

        // 1) 답변 존재 확인 (없으면 404 계열)
        Answer target = answerRepository.findById(answerId)
                .orElseThrow(() ->
                        new BusinessException(
                                AnswerErrorCode.ANSWER_NOT_FOUND,
                                "해당하는 답변을 찾을 수 없습니다. answerId = " + answerId
                        )
                );


        // 2) 답변이 해당 질문 소속인지 검증 (URL 조작 방지)
        if (!target.getQuestion().getId().equals(questionId)) {
            throw new BusinessException(
                    AnswerErrorCode.ANSWER_NOT_IN_QUESTION,
                    "answerId=" + answerId + ", questionId=" + questionId
            );
        }


        // 3) 질문 존재 확인 (answer가 question을 들고있긴 하지만, 명시적으로 확인)
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "해당하는 질문을 찾을 수 없습니다. questionId = " + questionId
                        )
                );


        // 4) 권한: 질문 작성자만 가능
        if (!question.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    QuestionErrorCode.ANSWER_ACCEPT_FORBIDDEN,
                    "questionOwnerId=" + question.getUser().getId() + ", requestUserId=" + userId
            );
        }


        // 5) 자기 답변 채택 금지
        if (target.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    QuestionErrorCode.SELF_ANSWER_ADOPT_NOT_ALLOWED,
                    "requestUserId=" + userId + ", answerOwnerId=" + target.getUser().getId()
            );
        }

        //이미 내가 채택한 답변이면 그냥 성공 처리(원하면)
        if (target.isAccepted()) {
            return;
        }

        // 6) 중복 채택 방지 (취소/변경 불가 정책)
        if (answerRepository.existsByQuestion_IdAndAcceptedTrue(questionId)) {
            throw new BusinessException(
                    QuestionErrorCode.ANSWER_ALREADY_ACCEPTED
            );
        }

        // 7) 채택 확정
        target.accept();
    }





}
