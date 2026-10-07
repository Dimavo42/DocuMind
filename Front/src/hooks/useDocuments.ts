import { useCallback, useEffect, useState } from 'react';
import { DOCUMENT_POLL_INTERVAL_MS } from '../constants/api';
import { IN_PROGRESS_STATUSES } from '../constants/documents';
import { documentApi } from '../services/documentApi';
import { toErrorMessage } from '../services/httpClient';
import type { DocumentInfo } from '../types/document';

export function useDocuments() {
  const [documents, setDocuments] = useState<DocumentInfo[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      setDocuments(await documentApi.getAll());
      setError(null);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  // Keep refreshing while any document is still being processed in the background.
  const hasDocumentsInProgress = documents.some((doc) => IN_PROGRESS_STATUSES.includes(doc.status));
  useEffect(() => {
    if (!hasDocumentsInProgress) {
      return;
    }
    const timer = setInterval(() => void refresh(), DOCUMENT_POLL_INTERVAL_MS);
    return () => clearInterval(timer);
  }, [hasDocumentsInProgress, refresh]);

  const upload = useCallback(
    async (file: File) => {
      setIsUploading(true);
      setError(null);
      try {
        await documentApi.upload(file);
        await refresh();
      } catch (err) {
        setError(toErrorMessage(err));
      } finally {
        setIsUploading(false);
      }
    },
    [refresh],
  );

  const remove = useCallback(
    async (id: number) => {
      setError(null);
      try {
        await documentApi.remove(id);
        setDocuments((current) => current.filter((doc) => doc.id !== id));
      } catch (err) {
        setError(toErrorMessage(err));
      }
    },
    [],
  );

  return { documents, isLoading, isUploading, error, upload, remove };
}
