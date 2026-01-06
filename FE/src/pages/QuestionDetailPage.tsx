import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { questionApi } from '@/api/question';
import { answerApi } from '@/api/answer';
import { commentApi } from '@/api/comment';
import { QuestionResponse } from '@/types';
import { formatDate, formatRelativeTime } from '@/utils/date';
import ReactionButton from '@/components/ReactionButton';
import { useAuthStore } from '@/store/authStore';
import toast from 'react-hot-toast';
import { Edit, Trash2, Flag, CheckCircle, MessageSquare, Send, ArrowLeft } from 'lucide-react';

export default function QuestionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuthStore();
  const [question, setQuestion] = useState<QuestionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState('');
  const [editContent, setEditContent] = useState('');
  const [answerContent, setAnswerContent] = useState('');
  const [commentContents, setCommentContents] = useState<Record<number, string>>({});
  const [showCommentInputs, setShowCommentInputs] = useState<Record<number, boolean>>({});
  const [editingAnswerId, setEditingAnswerId] = useState<number | null>(null);
  const [editAnswerContent, setEditAnswerContent] = useState<Record<number, string>>({});
  const [showComments, setShowComments] = useState<Record<number, boolean>>({});
  const [showAllComments, setShowAllComments] = useState<Record<number, boolean>>({});
  const [expandedAnswers, setExpandedAnswers] = useState<Record<number, boolean>>({});
  const [answerSortOrder, setAnswerSortOrder] = useState<'latest' | 'popular'>('latest');
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportTarget, setReportTarget] = useState<{ type: 'QUESTION' | 'ANSWER' | 'COMMENT'; id: number } | null>(null);
  const [reportCategory, setReportCategory] = useState<'SPAM' | 'ABUSE' | 'INAPPROPRIATE' | 'HARASSMENT' | 'OTHER'>('SPAM');
  const [reportReason, setReportReason] = useState('');
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editCommentContent, setEditCommentContent] = useState<Record<number, string>>({});

  useEffect(() => {
    if (id) {
      loadQuestion();
    }
  }, [id]);

  const loadQuestion = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await questionApi.getQuestion(Number(id));
      setQuestion(data);
      setEditTitle(data.title);
      setEditContent(data.content);
    } catch (error) {
      console.error('질문 로드 실패:', error);
      toast.error('질문을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!id || !confirm('정말 삭제하시겠습니까?')) return;
    try {
      await questionApi.delete(Number(id));
      toast.success('질문이 삭제되었습니다.');
      navigate('/');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '삭제에 실패했습니다.');
    }
  };

  const handleUpdate = async () => {
    if (!id) return;
    try {
      await questionApi.update(Number(id), {
        title: editTitle,
        content: editContent,
      });
      setIsEditing(false);
      await loadQuestion();
      toast.success('질문이 수정되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '수정에 실패했습니다.');
    }
  };

  const handleReport = async () => {
    if (!id) return;
    setReportTarget({ type: 'QUESTION', id: Number(id) });
    setShowReportModal(true);
  };

  const handleReportAnswer = async (answerId: number) => {
    if (!id) return;
    setReportTarget({ type: 'ANSWER', id: answerId });
    setShowReportModal(true);
  };

  const handleReportComment = async (commentId: number) => {
    setReportTarget({ type: 'COMMENT', id: commentId });
    setShowReportModal(true);
  };

  const handleUpdateComment = async (commentId: number) => {
    const content = editCommentContent[commentId];
    if (!content?.trim()) return;
    try {
      await commentApi.update(commentId, { content });
      setEditingCommentId(null);
      setEditCommentContent((prev) => {
        const newState = { ...prev };
        delete newState[commentId];
        return newState;
      });
      await loadQuestion();
      toast.success('댓글이 수정되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '댓글 수정에 실패했습니다.');
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    try {
      await commentApi.delete(commentId);
      await loadQuestion();
      toast.success('댓글이 삭제되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '삭제에 실패했습니다.');
    }
  };

  const submitReport = async () => {
    if (!reportTarget || !user) return;
    try {
      const { reportApi } = await import('@/api/report');
      await reportApi.create({
        reporterId: user.id || 0,
        reportedId: 0, // 백엔드에서 처리
        entityType: reportTarget.type,
        entityId: reportTarget.id,
        category: reportCategory,
        reasonDetail: reportReason,
      });
      toast.success('신고가 접수되었습니다.');
      setShowReportModal(false);
      setReportTarget(null);
      setReportCategory('SPAM');
      setReportReason('');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '신고에 실패했습니다.');
    }
  };

  const handleSubmitAnswer = async () => {
    if (!id || !answerContent.trim()) return;
    try {
      await answerApi.create(Number(id), { content: answerContent });
      setAnswerContent('');
      await loadQuestion();
      toast.success('답변이 등록되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '답변 등록에 실패했습니다.');
    }
  };

  const handleAcceptAnswer = async (answerId: number) => {
    if (!id) return;
    try {
      await questionApi.acceptAnswer(Number(id), answerId);
      await loadQuestion();
      toast.success('답변이 채택되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '채택에 실패했습니다.');
    }
  };

  const handleSubmitComment = async (answerId: number) => {
    const content = commentContents[answerId];
    if (!content?.trim()) return;
    try {
      await commentApi.create(answerId, { content });
      setCommentContents((prev) => ({ ...prev, [answerId]: '' }));
      setShowCommentInputs((prev) => ({ ...prev, [answerId]: false }));
      // 전체 새로고침 대신 해당 답변만 업데이트
      const updatedQuestion = await questionApi.getQuestion(Number(id));
      setQuestion(updatedQuestion);
      toast.success('댓글이 등록되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '댓글 등록에 실패했습니다.');
    }
  };

  const handleUpdateAnswer = async (answerId: number) => {
    const content = editAnswerContent[answerId];
    if (!content?.trim()) return;
    try {
      await answerApi.update(answerId, { content });
      setEditingAnswerId(null);
      setEditAnswerContent((prev) => {
        const newState = { ...prev };
        delete newState[answerId];
        return newState;
      });
      await loadQuestion();
      toast.success('답변이 수정되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '답변 수정에 실패했습니다.');
    }
  };

  const handleDeleteAnswer = async (answerId: number) => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    try {
      await answerApi.delete(answerId);
      await loadQuestion();
      toast.success('답변이 삭제되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '삭제에 실패했습니다.');
    }
  };

  const handleReportAnswer = async (answerId: number) => {
    if (!id) return;
    try {
      await answerApi.report(Number(id), answerId);
      toast.success('신고가 접수되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '신고에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <div className="text-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">로딩 중...</p>
      </div>
    );
  }

  if (!question) {
    return (
      <div className="card text-center py-12">
        <p className="text-gray-500">질문을 찾을 수 없습니다.</p>
        <Link to="/" className="mt-4 inline-block btn-primary">
          목록으로 돌아가기
        </Link>
      </div>
    );
  }

  // 본인 인식: user 객체의 nickname과 question의 nickname 비교
  const isOwner = user && question && user.nickname === question.nickname;

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <Link
          to="/"
          className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          <span>전체 목록 보기</span>
        </Link>
      </div>

      {/* 질문 */}
      <div className="card">
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            {isEditing ? (
              <div className="space-y-4">
                <input
                  type="text"
                  value={editTitle}
                  onChange={(e) => setEditTitle(e.target.value)}
                  className="input-field text-xl font-bold"
                />
                <textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={10}
                  className="input-field"
                />
                <div className="flex space-x-2">
                  <button onClick={handleUpdate} className="btn-primary">
                    저장
                  </button>
                  <button
                    onClick={() => {
                      setIsEditing(false);
                      setEditTitle(question.title);
                      setEditContent(question.content);
                    }}
                    className="btn-secondary"
                  >
                    취소
                  </button>
                </div>
              </div>
            ) : (
              <>
                <h1 className="text-3xl font-bold text-gray-900 mb-4">{question.title}</h1>
                <div className="flex items-center space-x-4 text-sm text-gray-500 mb-4">
                  <span className="font-medium text-gray-700">{question.nickname}</span>
                  <span>{formatDate(question.createdAt)}</span>
                  <span>조회 {question.viewCount}</span>
                </div>
                <div className="prose max-w-none mb-4">
                  <div 
                    className="text-gray-700 whitespace-pre-wrap"
                    dangerouslySetInnerHTML={{
                      __html: question.content
                        .replace(/```([\s\S]*?)```/g, '<pre class="bg-gray-100 p-4 rounded-lg overflow-x-auto my-4"><code>$1</code></pre>')
                        .replace(/\n/g, '<br />')
                    }}
                  />
                </div>
              </>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between pt-4 border-t border-gray-200">
          <ReactionButton
            key={`question-${question.id}-${isEditing}`}
            targetType="QUESTION"
            targetId={question.id}
            initialLikeCount={question.likeCount || 0}
            initialDislikeCount={question.dislikeCount || 0}
            initialMyReaction={question.myReactionType || 'NONE'}
          />
          <div className="flex space-x-2">
            {isOwner && !isEditing && (
              <>
                <button
                  onClick={() => setIsEditing(true)}
                  className="flex items-center space-x-1 px-3 py-1.5 text-gray-700 hover:text-primary-600 hover:bg-gray-100 rounded-lg transition-colors"
                >
                  <Edit className="w-4 h-4" />
                  <span>수정</span>
                </button>
                <button
                  onClick={handleDelete}
                  className="flex items-center space-x-1 px-3 py-1.5 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                  <span>삭제</span>
                </button>
              </>
            )}
            {isAuthenticated && !isOwner && user?.nickname !== question.nickname && (
              <button
                onClick={handleReport}
                className="flex items-center space-x-1 px-3 py-1.5 text-gray-700 hover:text-red-600 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <Flag className="w-4 h-4" />
                <span>신고</span>
              </button>
            )}
          </div>
        </div>

        {/* 답변 작성 */}
        {isAuthenticated && !isOwner ? (
          <div className="mt-6 pt-6 border-t border-gray-200">
            <h2 className="text-xl font-semibold mb-4">답변 작성</h2>
            <textarea
              value={answerContent}
              onChange={(e) => setAnswerContent(e.target.value)}
              rows={3}
              className="input-field mb-4 resize-none"
              placeholder="답변을 입력하세요..."
              style={{ minHeight: '80px', maxHeight: '150px' }}
            />
            <button onClick={handleSubmitAnswer} className="btn-primary">
              답변 등록
            </button>
          </div>
        ) : isAuthenticated && isOwner ? (
          <div className="mt-6 pt-6 border-t border-gray-200 bg-gray-50 p-4 rounded-lg">
            <p className="text-gray-600 text-sm">
              자신의 질문에는 답변을 작성할 수 없습니다.
            </p>
          </div>
        ) : null}

        {/* 답변 목록 - 질문 카드 안에 포함 */}
        <div className="mt-6 pt-6 border-t border-gray-200">
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-semibold">
                답변 {question.answers.length}개
              </h2>
          {question.answers.length > 0 && (
            <div className="flex items-center space-x-2">
              <button
                onClick={() => setAnswerSortOrder('latest')}
                className={`px-3 py-1 text-sm rounded-lg transition-colors ${
                  answerSortOrder === 'latest'
                    ? 'bg-primary-100 text-primary-700'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                최신순
              </button>
              <button
                onClick={() => setAnswerSortOrder('popular')}
                className={`px-3 py-1 text-sm rounded-lg transition-colors ${
                  answerSortOrder === 'popular'
                    ? 'bg-primary-100 text-primary-700'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                추천순
              </button>
            </div>
          )}
        </div>
        {[...question.answers]
          .sort((a, b) => {
            if (answerSortOrder === 'popular') {
              return (b.likeCount - b.dislikeCount) - (a.likeCount - a.dislikeCount);
            }
            return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
          })
          .map((answer) => {
          const isAnswerOwner = answer.nickname === user?.nickname;
          const isEditingAnswer = editingAnswerId === answer.id;
          
          return (
            <div
              key={answer.id}
              className={`card ${answer.accepted ? 'border-2 border-green-500 bg-gradient-to-br from-green-50 to-green-100 shadow-xl ring-2 ring-green-300' : 'border border-gray-200 bg-white'}`}
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <div className="flex items-center space-x-2 mb-2">
                    <span className="font-medium text-gray-900">{answer.nickname}</span>
                    {answer.accepted && (
                      <span className="px-3 py-1 bg-gradient-to-r from-green-500 to-green-600 text-white text-xs font-bold rounded-full flex items-center space-x-1 shadow-md">
                        <CheckCircle className="w-4 h-4" />
                        <span>채택된 답변</span>
                      </span>
                    )}
                    <span className="text-sm text-gray-500">
                      {formatRelativeTime(answer.createdAt)}
                    </span>
                  </div>
                  {isEditingAnswer ? (
                    <div className="space-y-2">
                      <textarea
                        value={editAnswerContent[answer.id] || answer.content}
                        onChange={(e) =>
                          setEditAnswerContent((prev) => ({
                            ...prev,
                            [answer.id]: e.target.value,
                          }))
                        }
                        rows={4}
                        className="input-field"
                      />
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleUpdateAnswer(answer.id)}
                          className="btn-primary"
                        >
                          저장
                        </button>
                        <button
                          onClick={() => {
                            setEditingAnswerId(null);
                            setEditAnswerContent((prev) => {
                              const newState = { ...prev };
                              delete newState[answer.id];
                              return newState;
                            });
                          }}
                          className="btn-secondary"
                        >
                          취소
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div>
                      {answer.content.length > 300 && !expandedAnswers[answer.id] ? (
                        <>
                          <div 
                            className="text-gray-700 whitespace-pre-wrap"
                            dangerouslySetInnerHTML={{
                              __html: answer.content.substring(0, 300).replace(/\n/g, '<br />') + '...'
                            }}
                          />
                          <button
                            onClick={() => setExpandedAnswers((prev) => ({ ...prev, [answer.id]: true }))}
                            className="mt-2 text-sm text-primary-600 hover:text-primary-700"
                          >
                            전체보기
                          </button>
                        </>
                      ) : (
                        <>
                          <div 
                            className="text-gray-700 whitespace-pre-wrap"
                            dangerouslySetInnerHTML={{
                              __html: answer.content
                                .replace(/```([\s\S]*?)```/g, '<pre class="bg-gray-100 p-4 rounded-lg overflow-x-auto my-4"><code>$1</code></pre>')
                                .replace(/\n/g, '<br />')
                            }}
                          />
                          {answer.content.length > 300 && expandedAnswers[answer.id] && (
                            <button
                              onClick={() => setExpandedAnswers((prev) => ({ ...prev, [answer.id]: false }))}
                              className="mt-2 text-sm text-gray-600 hover:text-gray-700"
                            >
                              접기
                            </button>
                          )}
                        </>
                      )}
                    </div>
                  )}
                </div>
              </div>

            {!isEditingAnswer && (
              <div className="flex items-center justify-between pt-4 border-t border-gray-200">
                <ReactionButton
                  targetType="ANSWER"
                  targetId={answer.id}
                  initialLikeCount={answer.likeCount}
                  initialDislikeCount={answer.dislikeCount}
                  initialMyReaction={answer.myReactionType || 'NONE'}
                />
                <div className="flex space-x-2">
                  {isAnswerOwner && (
                    <>
                      <button
                        onClick={() => {
                          setEditingAnswerId(answer.id);
                          setEditAnswerContent((prev) => ({
                            ...prev,
                            [answer.id]: answer.content,
                          }));
                        }}
                        className="flex items-center space-x-1 px-3 py-1.5 text-gray-700 hover:text-primary-600 hover:bg-gray-100 rounded-lg transition-colors"
                      >
                        <Edit className="w-4 h-4" />
                        <span>수정</span>
                      </button>
                      <button
                        onClick={() => handleDeleteAnswer(answer.id)}
                        className="flex items-center space-x-1 px-3 py-1.5 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                        <span>삭제</span>
                      </button>
                    </>
                  )}
                  {isOwner && !answer.accepted && !question.answers.some(a => a.accepted) && (
                    <button
                      onClick={() => handleAcceptAnswer(answer.id)}
                      className="flex items-center space-x-1 px-3 py-1.5 text-green-600 hover:text-green-700 hover:bg-green-50 rounded-lg transition-colors"
                    >
                      <CheckCircle className="w-4 h-4" />
                      <span>채택</span>
                    </button>
                  )}
                  {isAuthenticated && !isAnswerOwner && (
                    <button
                      onClick={() => handleReportAnswer(answer.id)}
                      className="flex items-center space-x-1 px-3 py-1.5 text-gray-700 hover:text-red-600 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                      <Flag className="w-4 h-4" />
                      <span>신고</span>
                    </button>
                  )}
                  {isAuthenticated && (
                    <button
                      onClick={() =>
                        setShowCommentInputs((prev) => ({
                          ...prev,
                          [answer.id]: !prev[answer.id],
                        }))
                      }
                      className="flex items-center space-x-1 px-3 py-1.5 text-gray-700 hover:text-primary-600 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                      <MessageSquare className="w-4 h-4" />
                      <span>댓글</span>
                    </button>
                  )}
                </div>
              </div>
            )}

            {/* 댓글 입력 */}
            {showCommentInputs[answer.id] && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <div className="flex space-x-2">
                  <input
                    type="text"
                    value={commentContents[answer.id] || ''}
                    onChange={(e) =>
                      setCommentContents((prev) => ({
                        ...prev,
                        [answer.id]: e.target.value,
                      }))
                    }
                    placeholder="댓글을 입력하세요..."
                    className="flex-1 input-field"
                    onKeyPress={(e) => {
                      if (e.key === 'Enter') {
                        handleSubmitComment(answer.id);
                      }
                    }}
                  />
                  <button
                    onClick={() => handleSubmitComment(answer.id)}
                    className="btn-primary flex items-center space-x-1"
                  >
                    <Send className="w-4 h-4" />
                    <span>등록</span>
                  </button>
                </div>
              </div>
            )}

            {/* 댓글 목록 */}
            {answer.comments && answer.comments.length > 0 && (
              <div className="mt-4 pt-4 border-t border-gray-200 pl-6 bg-gray-50 rounded-lg p-4">
                {!showAllComments[answer.id] ? (
                  <button
                    onClick={() => setShowAllComments((prev) => ({ ...prev, [answer.id]: true }))}
                    className="text-sm text-primary-600 hover:text-primary-700 mb-3"
                  >
                    댓글 {answer.comments.length}개 보기
                  </button>
                ) : (
                  <>
                    {answer.comments.length > 3 && !showComments[answer.id] ? (
                  <>
                    <div className="space-y-3">
                    {answer.comments.slice(0, 3).map((comment, idx) => (
                      <div key={comment.id} className={`flex items-start space-x-2 ${idx < 2 ? 'pb-3 border-b border-gray-300' : ''}`}>
                        <div className="flex-1">
                          <div className="flex items-center justify-between">
                            <span className="font-medium text-sm text-gray-600">
                              {comment.nickname}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600 mt-1">{comment.content}</p>
                          <span className="text-xs text-gray-400">
                            {formatRelativeTime(comment.createdAt)}
                          </span>
                        </div>
                      </div>
                    ))}
                    </div>
                    <button
                      onClick={() => setShowComments((prev) => ({ ...prev, [answer.id]: true }))}
                      className="mt-2 text-sm text-primary-600 hover:text-primary-700"
                    >
                      댓글 {answer.comments.length - 3}개 더보기
                    </button>
                  </>
                ) : (
                  <div className="space-y-3">
                    {answer.comments.map((comment, idx) => {
                      const isCommentOwner = comment.nickname === user?.nickname;
                      return (
                        <div key={comment.id} className={`flex items-start space-x-2 ${idx < answer.comments.length - 1 ? 'pb-3 border-b border-gray-300' : ''}`}>
                          <div className="flex-1">
                            <div className="flex items-center justify-between">
                              <span className="font-medium text-sm text-gray-600">
                                {comment.nickname}
                              </span>
                              {isAuthenticated && editingCommentId !== comment.id && (
                                <div className="flex items-center space-x-1">
                                  {isCommentOwner && (
                                    <>
                                      <button 
                                        onClick={() => {
                                          setEditingCommentId(comment.id);
                                          setEditCommentContent((prev) => ({
                                            ...prev,
                                            [comment.id]: comment.content,
                                          }));
                                        }}
                                        className="p-1 text-gray-400 hover:text-primary-600 transition-colors" 
                                        title="수정"
                                      >
                                        <Edit className="w-3 h-3" />
                                      </button>
                                      <button 
                                        onClick={() => handleDeleteComment(comment.id)}
                                        className="p-1 text-gray-400 hover:text-red-600 transition-colors" 
                                        title="삭제"
                                      >
                                        <Trash2 className="w-3 h-3" />
                                      </button>
                                    </>
                                  )}
                                  {!isCommentOwner && (
                                    <button 
                                      onClick={() => handleReportComment(comment.id)}
                                      className="p-1 text-gray-400 hover:text-red-600 transition-colors" 
                                      title="신고"
                                    >
                                      <Flag className="w-3 h-3" />
                                    </button>
                                  )}
                                </div>
                              )}
                            </div>
                            {editingCommentId === comment.id ? (
                              <div className="mt-2 space-y-2">
                                <input
                                  type="text"
                                  value={editCommentContent[comment.id] || comment.content}
                                  onChange={(e) =>
                                    setEditCommentContent((prev) => ({
                                      ...prev,
                                      [comment.id]: e.target.value,
                                    }))
                                  }
                                  className="w-full input-field text-sm"
                                />
                                <div className="flex space-x-2">
                                  <button
                                    onClick={() => handleUpdateComment(comment.id)}
                                    className="px-2 py-1 text-xs btn-primary"
                                  >
                                    저장
                                  </button>
                                  <button
                                    onClick={() => {
                                      setEditingCommentId(null);
                                      setEditCommentContent((prev) => {
                                        const newState = { ...prev };
                                        delete newState[comment.id];
                                        return newState;
                                      });
                                    }}
                                    className="px-2 py-1 text-xs btn-secondary"
                                  >
                                    취소
                                  </button>
                                </div>
                              </div>
                            ) : (
                              <>
                                <p className="text-sm text-gray-600 mt-1">{comment.content}</p>
                                <span className="text-xs text-gray-400">
                                  {formatRelativeTime(comment.createdAt)}
                                </span>
                              </>
                            )}
                          </div>
                        </div>
                      );
                    })}
                    {showComments[answer.id] && answer.comments.length > 3 && (
                      <button
                        onClick={() => setShowComments((prev) => ({ ...prev, [answer.id]: false }))}
                        className="mt-2 text-sm text-gray-600 hover:text-gray-700"
                      >
                        접기
                      </button>
                    )}
                  </div>
                )}
                    <button
                      onClick={() => setShowAllComments((prev) => ({ ...prev, [answer.id]: false }))}
                      className="mt-2 text-sm text-gray-600 hover:text-gray-700"
                    >
                      댓글 숨기기
                    </button>
                  </>
                )}
              </div>
            )}
          </div>
        );
        })}
            </div>
          </div>
        </div>
      </div>

      {/* 신고 모달 */}
      {showReportModal && (
        <>
          <div 
            className="fixed inset-0 bg-black bg-opacity-50 z-50"
            onClick={() => {
              setShowReportModal(false);
              setReportTarget(null);
              setReportCategory('SPAM');
              setReportReason('');
            }}
          />
          <div className="fixed inset-0 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6" onClick={(e) => e.stopPropagation()}>
              <h3 className="text-xl font-semibold mb-4">신고하기</h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    신고 유형
                  </label>
                  <select
                    value={reportCategory}
                    onChange={(e) => setReportCategory(e.target.value as any)}
                    className="w-full input-field"
                  >
                    <option value="SPAM">스팸</option>
                    <option value="ABUSE">욕설/비방</option>
                    <option value="INAPPROPRIATE">부적절한 내용</option>
                    <option value="HARASSMENT">괴롭힘</option>
                    <option value="OTHER">기타</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    신고 사유
                  </label>
                  <textarea
                    value={reportReason}
                    onChange={(e) => setReportReason(e.target.value)}
                    rows={4}
                    className="w-full input-field"
                    placeholder="신고 사유를 상세히 입력해주세요"
                  />
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => {
                      setShowReportModal(false);
                      setReportTarget(null);
                      setReportCategory('SPAM');
                      setReportReason('');
                    }}
                    className="flex-1 btn-secondary"
                  >
                    취소
                  </button>
                  <button
                    onClick={submitReport}
                    className="flex-1 btn-primary"
                  >
                    신고하기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

