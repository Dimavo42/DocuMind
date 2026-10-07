import type { ChatResponse } from '../types/chat';
import { SourceList } from './SourceList';
import { Spinner } from './Spinner';

interface AnswerPanelProps {
  response: ChatResponse | null;
  isAsking: boolean;
}

export function AnswerPanel({ response, isAsking }: AnswerPanelProps) {
  if (isAsking) {
    return (
      <div className="answer-panel">
        <Spinner label="Searching your documents and generating an answer..." />
      </div>
    );
  }
  if (!response) {
    return null;
  }
  return (
    <div className="answer-panel">
      <p className="answer-text">{response.answer}</p>
      <SourceList sources={response.sources} />
    </div>
  );
}
