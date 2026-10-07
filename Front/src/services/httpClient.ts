import { API_BASE_URL } from '../constants/api';
import type { ApiErrorBody } from '../types/api';

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, init);
  } catch {
    throw new ApiError(0, 'Cannot reach the DocuMind server. Is the backend running?');
  }

  if (!response.ok) {
    throw new ApiError(response.status, await readErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

async function readErrorMessage(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as Partial<ApiErrorBody>;
    if (body.message) {
      return body.message;
    }
  } catch {
    // The body was not JSON (e.g. a proxy error page); fall back to a generic message.
  }
  return `Request failed with status ${response.status}`;
}

export function toErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : 'Something went wrong';
}
