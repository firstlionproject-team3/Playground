import { useState } from 'react';
import { ThumbsUp, ThumbsDown } from 'lucide-react';
import { reactionApi } from '@/api/reaction';
import { TargetType, ReactionType } from '@/types';
import toast from 'react-hot-toast';

interface ReactionButtonProps {
  targetType: TargetType;
  targetId: number;
  initialLikeCount: number;
  initialDislikeCount: number;
  initialMyReaction?: 'LIKE' | 'DISLIKE' | 'NONE';
  onUpdate?: (likeCount: number, dislikeCount: number, myReaction: 'LIKE' | 'DISLIKE' | 'NONE') => void;
}

export default function ReactionButton({
  targetType,
  targetId,
  initialLikeCount,
  initialDislikeCount,
  initialMyReaction = 'NONE',
  onUpdate,
}: ReactionButtonProps) {
  const [likeCount, setLikeCount] = useState(initialLikeCount);
  const [dislikeCount, setDislikeCount] = useState(initialDislikeCount);
  const [myReaction, setMyReaction] = useState<'LIKE' | 'DISLIKE' | 'NONE'>(initialMyReaction);
  const [loading, setLoading] = useState(false);

  const handleReaction = async (reactionType: ReactionType) => {
    if (loading) return;
    
    setLoading(true);
    try {
      const request = {
        targetType,
        targetId,
        reactionType,
      };

      if (myReaction === reactionType) {
        // 같은 타입이면 삭제 (토글)
        await reactionApi.delete(targetType, targetId);
        if (reactionType === 'LIKE') {
          setLikeCount((prev) => Math.max(0, prev - 1));
          setMyReaction('NONE');
        } else {
          setDislikeCount((prev) => Math.max(0, prev - 1));
          setMyReaction('NONE');
        }
        onUpdate?.(likeCount - (reactionType === 'LIKE' ? 1 : 0), dislikeCount - (reactionType === 'DISLIKE' ? 1 : 0), 'NONE');
      } else {
        // 다른 타입이면 변경 또는 생성
        if (reactionType === 'LIKE') {
          await reactionApi.toggleLike(request);
          if (myReaction === 'DISLIKE') {
            setDislikeCount((prev) => Math.max(0, prev - 1));
            setLikeCount((prev) => prev + 1);
          } else {
            setLikeCount((prev) => prev + 1);
          }
          setMyReaction('LIKE');
          onUpdate?.(likeCount + (myReaction === 'DISLIKE' ? 0 : 1), dislikeCount - (myReaction === 'DISLIKE' ? 1 : 0), 'LIKE');
        } else {
          await reactionApi.toggleDislike(request);
          if (myReaction === 'LIKE') {
            setLikeCount((prev) => Math.max(0, prev - 1));
            setDislikeCount((prev) => prev + 1);
          } else {
            setDislikeCount((prev) => prev + 1);
          }
          setMyReaction('DISLIKE');
          onUpdate?.(likeCount - (myReaction === 'LIKE' ? 1 : 0), dislikeCount + (myReaction === 'LIKE' ? 0 : 1), 'DISLIKE');
        }
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || '반응 처리에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center space-x-2">
      <button
        onClick={() => handleReaction('LIKE')}
        disabled={loading}
        className={`flex items-center space-x-1 px-3 py-1.5 rounded-lg transition-colors ${
          myReaction === 'LIKE'
            ? 'bg-primary-100 text-primary-700'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
        } ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
      >
        <ThumbsUp className="w-4 h-4" />
        <span className="text-sm font-medium">{likeCount}</span>
      </button>
      <button
        onClick={() => handleReaction('DISLIKE')}
        disabled={loading}
        className={`flex items-center space-x-1 px-3 py-1.5 rounded-lg transition-colors ${
          myReaction === 'DISLIKE'
            ? 'bg-red-100 text-red-700'
            : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
        } ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
      >
        <ThumbsDown className="w-4 h-4" />
        <span className="text-sm font-medium">{dislikeCount}</span>
      </button>
    </div>
  );
}

