import { useState, type FormEvent } from 'react';

const MAX_QUESTION_LENGTH = 2000;

interface QuestionFormProps {
  isAsking: boolean;
  onAsk: (question: string) => void;
}

export function QuestionForm({ isAsking, onAsk }: QuestionFormProps) {
  const [question, setQuestion] = useState('');
  const trimmed = question.trim();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (trimmed && !isAsking) {
      onAsk(trimmed);
    }
  };

  return (
    <form className="question-form" onSubmit={handleSubmit}>
      <textarea
        value={question}
        onChange={(event) => setQuestion(event.target.value)}
        onKeyDown={(event) => {
          if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            event.currentTarget.form?.requestSubmit();
          }
        }}
        placeholder="Ask a question about your documents..."
        maxLength={MAX_QUESTION_LENGTH}
        rows={3}
        disabled={isAsking}
      />
      <button type="submit" className="button-primary" disabled={!trimmed || isAsking}>
        {isAsking ? 'Thinking...' : 'Ask'}
      </button>
    </form>
  );
}
