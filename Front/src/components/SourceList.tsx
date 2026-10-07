import type { SourceReference } from '../types/chat';
import { formatPercent } from '../utils/format';

interface SourceListProps {
  sources: SourceReference[];
}

export function SourceList({ sources }: SourceListProps) {
  if (sources.length === 0) {
    return null;
  }
  return (
    <details className="sources">
      <summary>Sources ({sources.length})</summary>
      <ol>
        {sources.map((source, index) => (
          <li key={`${source.documentId}-${source.chunkIndex}`}>
            <div className="source-header">
              <strong>
                Source {index + 1}: {source.documentName}
              </strong>
              <span className="muted">
                part {source.chunkIndex + 1} · {formatPercent(source.similarity)} match
              </span>
            </div>
            <p className="source-excerpt">{source.excerpt}</p>
          </li>
        ))}
      </ol>
    </details>
  );
}
