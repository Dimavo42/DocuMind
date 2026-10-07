import { IN_PROGRESS_STATUSES, STATUS_LABELS } from '../constants/documents';
import type { DocumentStatus } from '../types/document';

interface StatusBadgeProps {
  status: DocumentStatus;
}

export function StatusBadge({ status }: StatusBadgeProps) {
  const inProgress = IN_PROGRESS_STATUSES.includes(status);
  return (
    <span className={`status-badge status-${status.toLowerCase()}`}>
      {inProgress && <span className="spinner spinner-small" aria-hidden="true" />}
      {STATUS_LABELS[status]}
    </span>
  );
}
