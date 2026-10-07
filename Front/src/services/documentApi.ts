import { API_ENDPOINTS } from '../constants/api';
import type { DocumentInfo } from '../types/document';
import { request } from './httpClient';

export const documentApi = {
  getAll(): Promise<DocumentInfo[]> {
    return request<DocumentInfo[]>(API_ENDPOINTS.documents);
  },

  getById(id: number): Promise<DocumentInfo> {
    return request<DocumentInfo>(`${API_ENDPOINTS.documents}/${id}`);
  },

  upload(file: File): Promise<DocumentInfo> {
    const formData = new FormData();
    formData.append('file', file);
    return request<DocumentInfo>(API_ENDPOINTS.uploadDocument, { method: 'POST', body: formData });
  },

  remove(id: number): Promise<void> {
    return request<void>(`${API_ENDPOINTS.documents}/${id}`, { method: 'DELETE' });
  },
};
