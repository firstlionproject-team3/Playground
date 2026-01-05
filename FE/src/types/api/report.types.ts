import { EntityType, ReportCategory } from '../enum.types';
import { Page } from '../common.types';

export interface CreateReportRequest {
  reporterId: number;
  reportedId: number;
  entityType: EntityType;
  entityId: number;
  category: ReportCategory;
  reasonDetail?: string;
}

export interface ReportResponse {
  id: number;
  reporterId: number;
  reportedId: number;
  entityType: EntityType;
  entityId: number;
  category: ReportCategory;
  reasonDetail?: string;
  status: string;
  createdAt: string;
}

export type ReportPage = Page<ReportResponse>;

