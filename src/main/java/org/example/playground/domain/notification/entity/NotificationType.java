package org.example.playground.domain.notification.entity;

public enum NotificationType {
    ACCEPTED_ANSWER,   // 내 답변이 채택됨
    REPORT_RECEIVED,   // 신고 접수 (운영자 알림)
    NEW_ANSWER,        // 내 질문에 답변이 달림
    NEW_COMMENT,        // 내 게시물에 댓글이 달림
    QUESTION_DELETED_BY_ADMIN,  // 관리자가 질문을 삭제
    ANSWER_DELETED_BY_ADMIN     // 관리자가 답변을 삭제
}