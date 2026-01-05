import type { ErrorResponse } from '@/types';

export const getErrorMessage = (error: unknown): string => {
  if (typeof error === 'string') return error;
  
  if (error && typeof error === 'object' && 'response' in error) {
    const axiosError = error as { response?: { data?: ErrorResponse } };
    const errorData = axiosError.response?.data;
    
    if (errorData) {
      if (errorData.details) {
        const detailMessages = Object.values(errorData.details).join(', ');
        return detailMessages || errorData.message;
      }
      return errorData.message || '오류가 발생했습니다.';
    }
  }
  
  return '알 수 없는 오류가 발생했습니다.';
};

