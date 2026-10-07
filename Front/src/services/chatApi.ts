import { API_ENDPOINTS } from '../constants/api';
import type { ChatRequest, ChatResponse } from '../types/chat';
import { request } from './httpClient';

export const chatApi = {
  ask(chatRequest: ChatRequest): Promise<ChatResponse> {
    return request<ChatResponse>(API_ENDPOINTS.chat, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(chatRequest),
    });
  },
};
