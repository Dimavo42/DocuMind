export type DocumentStatus = 'UPLOADED' | 'PROCESSING' | 'READY' | 'FAILED';

export type DocumentType = 'PDF' | 'TXT' | 'DOCX' | 'UNKNOWN';

export interface DocumentInfo {
  id: number;
  filename: string;
  type: DocumentType;
  status: DocumentStatus;
  sizeBytes: number;
  chunkCount: number;
  errorMessage: string | null;
  createdAt: string;
  updatedAt: string;
}
