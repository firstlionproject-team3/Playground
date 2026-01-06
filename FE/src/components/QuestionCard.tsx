import { Link } from 'react-router-dom';
import { formatRelativeTime } from '@/utils/date';
import { QuestionSummary } from '@/types';

interface QuestionCardProps {
  question: QuestionSummary;
}

export default function QuestionCard({ question }: QuestionCardProps) {
  return (
    <Link
      to={`/questions/${question.id}`}
      className="block card hover:shadow-lg transition-shadow duration-200"
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center space-x-2 mb-2">
            <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">
              {question.title}
            </h3>
            {question.answerCount !== undefined && (
              <span className="text-sm text-gray-400 whitespace-nowrap">
                ({question.answerCount})
              </span>
            )}
          </div>
          <div className="flex items-center space-x-4 text-sm text-gray-500">
            <span className="flex items-center space-x-1">
              <span className="font-medium text-gray-700">{question.nickname}</span>
            </span>
            <span>{formatRelativeTime(question.createdAt)}</span>
          </div>
        </div>
      </div>
    </Link>
  );
}

