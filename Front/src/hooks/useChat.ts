import { useCallback, useState } from 'react';
import { chatApi } from '../services/chatApi';
import { toErrorMessage } from '../services/httpClient';
import type { ChatResponse } from '../types/chat';

export function useChat() {
  const [response, setResponse] = useState<ChatResponse | null>(null);
  const [isAsking, setIsAsking] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const ask = useCallback(async (question: string) => {
    setIsAsking(true);
    setError(null);
    setResponse(null);
    try {
      setResponse(await chatApi.ask({ question }));
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsAsking(false);
    }
  }, []);

  return { response, isAsking, error, ask };
}
