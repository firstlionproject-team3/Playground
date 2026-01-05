import { apiClient } from '../axios/instance';
import type { CreateReportRequest, ReportResponse, ReportPage } from '@/types';

export const reportApi = {
  create: async (data: CreateReportRequest): Promise<ReportResponse> => {
    const response = await apiClient.post<ReportResponse>('/reports', data);
    return response.data;
  },

  getList: async (page = 0, size = 10): Promise<ReportPage> => {
    const response = await apiClient.get<ReportPage>(`/reports?page=${page}&size=${size}`);
    return response.data;
  },

  approve: async (reportId: number): Promise<void> => {
    await apiClient.patch(`/reports/${reportId}/approve`);
  },

  reject: async (reportId: number): Promise<void> => {
    await apiClient.patch(`/reports/${reportId}/reject`);
  },
};

