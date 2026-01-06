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
import { Edit, Trash2, Flag, CheckCircle, MessageSquare, Send } from 'lucide-react';

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
    try {
      await questionApi.report(Number(id));
      toast.success('신고가 접수되었습니다.');
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
      await loadQuestion();
      toast.success('댓글이 등록되었습니다.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || '댓글 등록에 실패했습니다.');
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

  const isOwner = question.nickname === user?.nickname;

  return (
    <div className="space-y-6">
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
                  <p className="text-gray-700 whitespace-pre-wrap">{question.content}</p>
                </div>
              </>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between pt-4 border-t border-gray-200">
          <ReactionButton
            targetType="QUESTION"
            targetId={question.id}
            initialLikeCount={0}
            initialDislikeCount={0}
            initialMyReaction="NONE"
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
            {isAuthenticated && !isOwner && (
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
      </div>

      {/* 답변 작성 */}
      {isAuthenticated && !isOwner && (
        <div className="card">
          <h2 className="text-xl font-semibold mb-4">답변 작성</h2>
          <textarea
            value={answerContent}
            onChange={(e) => setAnswerContent(e.target.value)}
            rows={6}
            className="input-field mb-4"
            placeholder="답변을 입력하세요..."
          />
          <button onClick={handleSubmitAnswer} className="btn-primary">
            답변 등록
          </button>
        </div>
      )}

      {/* 답변 목록 */}
      <div className="space-y-4">
        <h2 className="text-2xl font-semibold">
          답변 {question.answers.length}개
        </h2>
        {question.answers.map((answer) => (
          <div
            key={answer.id}
            className={`card ${answer.accepted ? 'border-2 border-green-500 bg-green-50' : ''}`}
          >
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <div className="flex items-center space-x-2 mb-2">
                  <span className="font-medium text-gray-900">{answer.nickname}</span>
                  {answer.accepted && (
                    <span className="badge bg-green-100 text-green-800 flex items-center space-x-1">
                      <CheckCircle className="w-3 h-3" />
                      <span>채택됨</span>
                    </span>
                  )}
                  <span className="text-sm text-gray-500">
                    {formatRelativeTime(answer.createdAt)}
                  </span>
                </div>
                <p className="text-gray-700 whitespace-pre-wrap">{answer.content}</p>
              </div>
            </div>

            <div className="flex items-center justify-between pt-4 border-t border-gray-200">
              <ReactionButton
                targetType="ANSWER"
                targetId={answer.id}
                initialLikeCount={answer.likeCount}
                initialDislikeCount={answer.dislikeCount}
                initialMyReaction={answer.myReactionType || 'NONE'}
              />
              <div className="flex space-x-2">
                {isOwner && !answer.accepted && (
                  <button
                    onClick={() => handleAcceptAnswer(answer.id)}
                    className="flex items-center space-x-1 px-3 py-1.5 text-green-600 hover:text-green-700 hover:bg-green-50 rounded-lg transition-colors"
                  >
                    <CheckCircle className="w-4 h-4" />
                    <span>채택</span>
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
              <div className="mt-4 pt-4 border-t border-gray-200 space-y-2">
                {answer.comments.map((comment) => (
                  <div key={comment.id} className="flex items-start space-x-2">
                    <div className="flex-1">
                      <span className="font-medium text-sm text-gray-900">
                        {comment.nickname}
                      </span>
                      <p className="text-sm text-gray-700">{comment.content}</p>
                      <span className="text-xs text-gray-500">
                        {formatRelativeTime(comment.createdAt)}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

