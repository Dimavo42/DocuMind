import { AnswerPanel } from '../components/AnswerPanel';
import { DocumentList } from '../components/DocumentList';
import { DocumentUpload } from '../components/DocumentUpload';
import { ErrorMessage } from '../components/ErrorMessage';
import { QuestionForm } from '../components/QuestionForm';
import { useChat } from '../hooks/useChat';
import { useDocuments } from '../hooks/useDocuments';

export function HomePage() {
  const documents = useDocuments();
  const chat = useChat();

  return (
    <main className="page">
      <header className="page-header">
        <h1>DocuMind</h1>
        <p className="muted">Ask questions about your documents with a local AI model.</p>
      </header>

      <div className="layout">
        <section className="card">
          <h2>Documents</h2>
          <DocumentUpload isUploading={documents.isUploading} onUpload={documents.upload} />
          <ErrorMessage message={documents.error} />
          <DocumentList
            documents={documents.documents}
            isLoading={documents.isLoading}
            onDelete={documents.remove}
          />
        </section>

        <section className="card">
          <h2>Ask a question</h2>
          <QuestionForm isAsking={chat.isAsking} onAsk={chat.ask} />
          <ErrorMessage message={chat.error} />
          <AnswerPanel response={chat.response} isAsking={chat.isAsking} />
        </section>
      </div>
    </main>
  );
}
