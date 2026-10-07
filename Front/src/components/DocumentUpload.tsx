import { useRef, useState, type ChangeEvent, type DragEvent } from 'react';
import { ACCEPTED_FILE_EXTENSIONS } from '../constants/documents';
import { Spinner } from './Spinner';

interface DocumentUploadProps {
  isUploading: boolean;
  onUpload: (file: File) => void;
}

export function DocumentUpload({ isUploading, onUpload }: DocumentUploadProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [isDragging, setIsDragging] = useState(false);

  const handleFile = (file: File | undefined) => {
    if (file && !isUploading) {
      onUpload(file);
    }
  };

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    handleFile(event.target.files?.[0]);
    event.target.value = '';
  };

  const handleDrop = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setIsDragging(false);
    handleFile(event.dataTransfer.files[0]);
  };

  return (
    <div
      className={`upload-zone ${isDragging ? 'upload-zone-active' : ''}`}
      onDragOver={(event) => {
        event.preventDefault();
        setIsDragging(true);
      }}
      onDragLeave={() => setIsDragging(false)}
      onDrop={handleDrop}
      onClick={() => inputRef.current?.click()}
      role="button"
      tabIndex={0}
      onKeyDown={(event) => {
        if (event.key === 'Enter' || event.key === ' ') {
          inputRef.current?.click();
        }
      }}
    >
      <input
        ref={inputRef}
        type="file"
        accept={ACCEPTED_FILE_EXTENSIONS}
        onChange={handleChange}
        hidden
      />
      {isUploading ? (
        <Spinner label="Uploading..." />
      ) : (
        <>
          <strong>Drop a file here or click to choose</strong>
          <span className="muted">PDF, TXT or DOCX</span>
        </>
      )}
    </div>
  );
}
