# DocuMind

A local AI document assistant. Upload PDF, TXT or DOCX files and ask questions about them.
Everything runs on your machine: answers come from a local [Ollama](https://ollama.com) model, and
document chunks are stored as vectors in PostgreSQL with [pgvector](https://github.com/pgvector/pgvector).

## Architecture

DocuMind uses a simple **RAG** (retrieval-augmented generation) flow.

```text
React Frontend (Vite + TypeScript)
      |
      v
Spring Boot REST API
      |
      +--> Document processing (extract text -> split into chunks)
      |
      +--> Embedding generation through Ollama  (nomic-embed-text)
      |
      +--> PostgreSQL + pgvector  (stores chunks + embeddings)
      |
      +--> Retrieve the most similar chunks (cosine distance)
      |
      +--> Ollama local LLM  (llama3.2)
      |
      v
Answer + sources returned to React
```

**Upload flow:** save metadata (`UPLOADED`) → background job sets `PROCESSING` → extract text →
split into overlapping chunks → embed every chunk with Ollama → store chunks and vectors → `READY`
(or `FAILED` with an error message). The frontend polls the document list while a document is processing.

**Question flow:** embed the question → find the top-K most similar chunks in pgvector → build a prompt
with those chunks as context → ask the Ollama chat model → return the answer and the source chunks.

## Technologies

| Layer    | Technology |
|----------|------------|
| Backend  | Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, Bean Validation, Maven |
| Vectors  | PostgreSQL 16 + pgvector, Hibernate `hibernate-vector` |
| Parsing  | Apache PDFBox (PDF), Apache POI (DOCX) |
| AI       | Ollama (local), called through Spring's `RestClient` |
| Frontend | React 19, TypeScript, Vite |
| Runtime  | Docker Compose, Nginx (serves the frontend and proxies `/api`) |

## Folder structure

```text
DocuMind/
├── Backend/
│   ├── src/main/java/com/documind/
│   │   ├── controller/          REST endpoints (HTTP only)
│   │   ├── service/             Business logic, RAG orchestration, Ollama services
│   │   │   └── extraction/      PDF / TXT / DOCX text extractors
│   │   ├── repository/          Spring Data repositories + vector similarity query
│   │   │   └── projection/      Read model for vector search results
│   │   ├── entity/              JPA entities: Document, DocumentChunk
│   │   ├── dto/request|response API models (records)
│   │   ├── mapper/              Entity/projection -> DTO mapping
│   │   ├── config/              Typed properties, RestClient, async executor, CORS
│   │   ├── client/ollama/       Low-level Ollama HTTP client + its DTOs
│   │   ├── exception/           Custom exceptions + @RestControllerAdvice handler
│   │   ├── enums/               DocumentStatus, DocumentType, ChatRole
│   │   └── util/                Text chunking, normalization, vector formatting
│   ├── src/main/resources/      application.yml, schema.sql
│   ├── Dockerfile
│   ├── mvnw / mvnw.cmd          Maven wrapper (no Maven install needed)
│   └── pom.xml
├── Front/
│   ├── src/
│   │   ├── components/          UI pieces (upload, list, question form, answer...)
│   │   ├── pages/               HomePage
│   │   ├── services/            httpClient, documentApi, chatApi
│   │   ├── hooks/               useDocuments, useChat
│   │   ├── types/               API types
│   │   ├── constants/           Endpoints, statuses, polling interval
│   │   ├── utils/               Formatting helpers
│   │   └── App.tsx
│   ├── nginx.conf
│   └── Dockerfile
├── docker/postgres/init.sql     Enables the vector extension
├── docker-compose.yml
└── .env.example
```

## Required software

- [Ollama](https://ollama.com/download) — runs the AI models locally
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) — for Docker Compose / PostgreSQL
- For running without Docker: **Java 21** and **Node.js 20.19+ or 22.12+**
  (Maven is not required, the project includes the Maven wrapper)

## 1. Install Ollama and pull the models

Download and install Ollama from <https://ollama.com/download>, then pull the two models:

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

- `llama3.2` is the chat model that writes the answers.
- `nomic-embed-text` is the embedding model that turns text into vectors.

Check that Ollama is running:

```bash
ollama list
```

You can use other models by changing `OLLAMA_CHAT_MODEL` / `OLLAMA_EMBEDDING_MODEL`. The vector column has no fixed
dimension, so switching embedding models works without a schema change. Re-upload your documents afterwards,
because vectors from different embedding models cannot be compared.

## 2. Run everything with Docker Compose (recommended)

From the `DocuMind` folder:

```bash
docker compose up --build
```

Then open <http://localhost:3000>.

| Service    | URL / port |
|------------|------------|
| Frontend   | http://localhost:3000 |
| Backend    | http://localhost:8080 |
| PostgreSQL | localhost:5432 (user/password/db: `documind`) |

Ollama is **not** in Docker. The backend reaches the Ollama on your machine through
`http://host.docker.internal:11434`.

To override settings, copy `.env.example` to `.env` and edit it. To stop: `docker compose down`
(add `-v` to also delete the database).

## 3. Run manually (for development)

### Start PostgreSQL only

```bash
docker compose up -d postgres
```

### Backend

```powershell
cd Backend
.\mvnw.cmd spring-boot:run
```

(on macOS/Linux: `./mvnw spring-boot:run`). The API starts on <http://localhost:8080>.
Defaults in `application.yml` already point to `localhost` for PostgreSQL and Ollama.

### Frontend

```bash
cd Front
npm install
npm run dev
```

Open <http://localhost:5173>. Vite forwards `/api` requests to `http://localhost:8080`.

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/documind` | JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | `documind` / `documind` | Database credentials |
| `OLLAMA_BASE_URL` | `http://localhost:11434` (Docker: `http://host.docker.internal:11434`) | Ollama API URL |
| `OLLAMA_CHAT_MODEL` | `llama3.2` | Model that generates answers |
| `OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Model that creates embeddings |
| `OLLAMA_READ_TIMEOUT` | `5m` | Max time to wait for Ollama |
| `RAG_CHUNK_SIZE` | `1000` | Chunk size in characters |
| `RAG_CHUNK_OVERLAP` | `200` | Characters shared between neighbouring chunks |
| `RAG_TOP_K` | `5` | Number of chunks sent to the LLM as context |
| `RAG_EMBEDDING_BATCH_SIZE` | `16` | Chunks embedded per Ollama request |
| `MAX_FILE_SIZE` | `25MB` | Upload size limit |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Allowed frontend origins |
| `VITE_DEV_BACKEND_URL` | `http://localhost:8080` | (Frontend dev only) where Vite proxies `/api` |

## API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/documents/upload` | Upload a file (multipart field `file`). Returns `202 Accepted`. |
| `GET` | `/api/documents` | List all documents |
| `GET` | `/api/documents/{id}` | Get one document |
| `DELETE` | `/api/documents/{id}` | Delete a document and its chunks |
| `POST` | `/api/chat` | Ask a question |

### Example calls

Upload a document:

```bash
curl -F "file=@notes.pdf" http://localhost:8080/api/documents/upload
```

```json
{ "id": 1, "filename": "notes.pdf", "type": "PDF", "status": "UPLOADED", "sizeBytes": 48213, "chunkCount": 0, ... }
```

List documents (wait until `status` is `READY`):

```bash
curl http://localhost:8080/api/documents
```

Ask a question:

```bash
curl -X POST http://localhost:8080/api/chat -H "Content-Type: application/json" -d "{\"question\": \"What does the document say about authentication?\"}"
```

```json
{
  "answer": "The document says authentication uses JWT tokens that expire after 15 minutes (Source 1).",
  "sources": [
    { "documentId": 1, "documentName": "notes.pdf", "chunkIndex": 4, "similarity": 0.812, "excerpt": "..." }
  ]
}
```

Delete a document:

```bash
curl -X DELETE http://localhost:8080/api/documents/1
```

Errors use one format:

```json
{ "status": 400, "error": "Bad Request", "message": "question: must not be empty", "timestamp": "..." }
```

## Troubleshooting

- **"The local AI model is unavailable"**: make sure Ollama is running (`ollama list`) and both models are pulled.
- **Backend in Docker cannot reach Ollama**: set the environment variable `OLLAMA_HOST=0.0.0.0` on your machine,
  restart Ollama, and try again.
- **Document stays `FAILED`**: the error message is shown in the document list. Scanned PDFs (images only)
  contain no text, so they cannot be processed.
- **The first answer is slow**: Ollama loads the model into memory on the first request.
