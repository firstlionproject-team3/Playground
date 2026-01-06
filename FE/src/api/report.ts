import apiClient from './client';
import { ReportCreateRequest, ReportResponse, PageResponse } from '@/types';

export const reportApi = {
  // 신고 생성
  create: async (data: ReportCreateRequest): Promise<ReportResponse> => {
    const response = await apiClient.post<ReportResponse>('/reports', data);
    return response.data;
  },

  // 대기 중인 신고 목록 조회 (관리자)
  getReports: async (page = 0, size = 10): Promise<PageResponse<ReportResponse>> => {
    const response = await apiClient.get<PageResponse<ReportResponse>>(
      `/reports?page=${page}&size=${size}`
    );
    return response.data;
  },

  // 신고 승인 (관리자)
  approve: async (reportId: number): Promise<void> => {
    await apiClient.patch(`/reports/${reportId}/approve`);
  },

  // 신고 거부 (관리자)
  reject: async (reportId: number): Promise<void> => {
    await apiClient.patch(`/reports/${reportId}/reject`);
  },
};

