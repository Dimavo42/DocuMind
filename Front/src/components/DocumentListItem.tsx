import type { DocumentInfo } from '../types/document';
import { formatBytes } from '../utils/format';
import { StatusBadge } from './StatusBadge';

interface DocumentListItemProps {
  document: DocumentInfo;
  onDelete: (id: number) => void;
}

export function DocumentListItem({ document, onDelete }: DocumentListItemProps) {
  return (
    <li className="document-item">
      <div className="document-info">
        <span className="document-name" title={document.filename}>
          {document.filename}
        </span>
        <span className="muted">
          {document.type} · {formatBytes(document.sizeBytes)}
          {document.status === 'READY' && ` · ${document.chunkCount} chunks`}
        </span>
        {document.status === 'FAILED' && document.errorMessage && (
          <span className="document-error">{document.errorMessage}</span>
        )}
      </div>
      <div className="document-actions">
        <StatusBadge status={document.status} />
        <button
          type="button"
          className="button-ghost"
          onClick={() => onDelete(document.id)}
          aria-label={`Delete ${document.filename}`}
        >
          Delete
        </button>
      </div>
    </li>
  );
}
