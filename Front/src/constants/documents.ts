import type { DocumentStatus } from '../types/document';

export const ACCEPTED_FILE_EXTENSIONS = '.pdf,.txt,.docx';

export const IN_PROGRESS_STATUSES: readonly DocumentStatus[] = ['UPLOADED', 'PROCESSING'];

export const STATUS_LABELS: Record<DocumentStatus, string> = {
  UPLOADED: 'Uploaded',
  PROCESSING: 'Processing',
  READY: 'Ready',
  FAILED: 'Failed',
};
