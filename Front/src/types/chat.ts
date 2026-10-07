export interface ChatRequest {
  question: string;
}

export interface SourceReference {
  documentId: number;
  documentName: string;
  chunkIndex: number;
  similarity: number;
  excerpt: string;
}

export interface ChatResponse {
  answer: string;
  sources: SourceReference[];
}
