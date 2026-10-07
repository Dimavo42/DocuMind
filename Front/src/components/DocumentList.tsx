import type { DocumentInfo } from '../types/document';
import { DocumentListItem } from './DocumentListItem';
import { Spinner } from './Spinner';

interface DocumentListProps {
  documents: DocumentInfo[];
  isLoading: boolean;
  onDelete: (id: number) => void;
}

export function DocumentList({ documents, isLoading, onDelete }: DocumentListProps) {
  if (isLoading) {
    return <Spinner label="Loading documents..." />;
  }
  if (documents.length === 0) {
    return <p className="muted">No documents yet. Upload one to get started.</p>;
  }
  return (
    <ul className="document-list">
      {documents.map((document) => (
        <DocumentListItem key={document.id} document={document} onDelete={onDelete} />
      ))}
    </ul>
  );
}
