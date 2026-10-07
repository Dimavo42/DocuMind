package com.documind.service;

import com.documind.dto.response.DocumentResponse;
import com.documind.entity.Document;
import com.documind.enums.DocumentType;
import com.documind.exception.DocumentNotFoundException;
import com.documind.exception.InvalidDocumentException;
import com.documind.mapper.DocumentMapper;
import com.documind.repository.DocumentChunkRepository;
import com.documind.repository.DocumentRepository;
import com.documind.service.extraction.TextExtractionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final DocumentProcessingService processingService;
    private final TextExtractionService textExtractionService;
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentChunkRepository chunkRepository,
                           DocumentProcessingService processingService,
                           TextExtractionService textExtractionService,
                           DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.processingService = processingService;
        this.textExtractionService = textExtractionService;
        this.documentMapper = documentMapper;
    }

    /**
     * Saves the document metadata and starts background processing. Returns immediately with status UPLOADED.
     */
    public DocumentResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDocumentException("The uploaded file is empty");
        }

        String filename = cleanFilename(file.getOriginalFilename());
        DocumentType type = DocumentType.fromFilename(filename);
        if (!textExtractionService.supports(type)) {
            throw new InvalidDocumentException("Unsupported file type. Supported types: PDF, TXT, DOCX");
        }

        byte[] content = readBytes(file);
        Document document = documentRepository.save(
                new Document(filename, file.getContentType(), type, file.getSize()));

        processingService.process(document.getId(), content);
        return documentMapper.toResponse(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findAll() {
        return documentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponse findById(Long id) {
        return documentMapper.toResponse(getDocument(id));
    }

    @Transactional
    public void delete(Long id) {
        Document document = getDocument(id);
        chunkRepository.deleteByDocumentId(document.getId());
        documentRepository.delete(document);
    }

    private Document getDocument(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));
    }

    private static String cleanFilename(String originalFilename) {
        String filename = StringUtils.getFilename(StringUtils.cleanPath(
                originalFilename == null ? "" : originalFilename));
        if (!StringUtils.hasText(filename)) {
            throw new InvalidDocumentException("The uploaded file has no name");
        }
        return filename;
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new InvalidDocumentException("Could not read the uploaded file");
        }
    }
}
