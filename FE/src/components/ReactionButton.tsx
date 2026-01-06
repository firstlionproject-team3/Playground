import { useState, useEffect } from 'react';
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

  // Props 변경 시 state 업데이트
  useEffect(() => {
    setLikeCount(initialLikeCount);
    setDislikeCount(initialDislikeCount);
    setMyReaction(initialMyReaction);
  }, [initialLikeCount, initialDislikeCount, initialMyReaction]);

  const handleReaction = async (reactionType: ReactionType) => {
    if (loading) return;
    
    setLoading(true);
    try {
      const request = {
        targetType,
        targetId,
        reactionType,
      };

      const currentLikeCount = likeCount;
      const currentDislikeCount = dislikeCount;
      const currentMyReaction = myReaction;

      if (currentMyReaction === reactionType) {
        // 같은 타입이면 삭제 (토글)
        await reactionApi.delete(targetType, targetId);
        if (reactionType === 'LIKE') {
          setLikeCount((prev) => Math.max(0, prev - 1));
          setMyReaction('NONE');
          onUpdate?.(currentLikeCount - 1, currentDislikeCount, 'NONE');
        } else {
          setDislikeCount((prev) => Math.max(0, prev - 1));
          setMyReaction('NONE');
          onUpdate?.(currentLikeCount, currentDislikeCount - 1, 'NONE');
        }
      } else {
        // 다른 타입이면 변경 또는 생성
        if (reactionType === 'LIKE') {
          await reactionApi.toggleLike(request);
          if (currentMyReaction === 'DISLIKE') {
            setDislikeCount((prev) => Math.max(0, prev - 1));
            setLikeCount((prev) => prev + 1);
            onUpdate?.(currentLikeCount + 1, currentDislikeCount - 1, 'LIKE');
          } else {
            setLikeCount((prev) => prev + 1);
            onUpdate?.(currentLikeCount + 1, currentDislikeCount, 'LIKE');
          }
          setMyReaction('LIKE');
        } else {
          await reactionApi.toggleDislike(request);
          if (currentMyReaction === 'LIKE') {
            setLikeCount((prev) => Math.max(0, prev - 1));
            setDislikeCount((prev) => prev + 1);
            onUpdate?.(currentLikeCount - 1, currentDislikeCount + 1, 'DISLIKE');
          } else {
            setDislikeCount((prev) => prev + 1);
            onUpdate?.(currentLikeCount, currentDislikeCount + 1, 'DISLIKE');
          }
          setMyReaction('DISLIKE');
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

