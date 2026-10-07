// Empty by default: requests go to /api on the same origin (Vite proxy in dev, Nginx in Docker).
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

export const API_ENDPOINTS = {
  documents: '/api/documents',
  uploadDocument: '/api/documents/upload',
  chat: '/api/chat',
} as const;

export const DOCUMENT_POLL_INTERVAL_MS = 2000;
