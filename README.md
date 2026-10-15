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

<img width="1446" height="771" alt="image" src="https://github.com/user-attachments/assets/8ba01209-7a39-484a-9637-d980bc86204f" />



