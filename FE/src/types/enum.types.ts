export type TargetType = 'QUESTION' | 'ANSWER' | 'COMMENT';

export type ReactionType = 'LIKE' | 'DISLIKE' | 'NONE';

export type NotificationType =
  | 'NEW_ANSWER'
  | 'REPORT_RECEIVED'
  | 'NEW_COMMENT'
  | 'ANSWER_ACCEPTED';

export type EntityType = 'QUESTION' | 'ANSWER' | 'COMMENT' | 'USER';

export type ReportCategory =
  | 'SPAM'
  | 'ABUSE'
  | 'INAPPROPRIATE'
  | 'OFF_TOPIC'
  | 'DUPLICATE';

export type SearchType = 'title' | 'content' | 'all';

