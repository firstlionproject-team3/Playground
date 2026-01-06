import { format, formatDistanceToNow, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';

export const formatDate = (dateString: string): string => {
  try {
    const date = parseISO(dateString);
    return format(date, 'yyyy년 MM월 dd일', { locale: ko });
  } catch {
    return dateString;
  }
};

export const formatRelativeTime = (dateString: string): string => {
  try {
    const date = parseISO(dateString);
    return formatDistanceToNow(date, { addSuffix: true, locale: ko });
  } catch {
    return dateString;
  }
};

