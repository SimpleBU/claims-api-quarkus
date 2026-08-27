package com.example.claims.service;

import com.example.claims.dto.ClaimDocument;
import com.example.claims.dto.DocumentKind;
import com.example.claims.dto.DocumentUploadResult;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DocumentService {

    private final Map<String, ClaimDocument> documents = new ConcurrentHashMap<>();
    private final IdSequence ids = new IdSequence("doc", 2000);

    public DocumentService() {
        store("clm-901", DocumentKind.POLICE_REPORT, "fire-report.pdf", "application/pdf", 184320L);
        store("clm-901", DocumentKind.PHOTO, "kitchen-1.jpg", "image/jpeg", 921600L);
        store("clm-902", DocumentKind.MEDICAL_REPORT, "discharge.pdf", "application/pdf", 245760L);
    }

    private ClaimDocument store(String claimId, DocumentKind kind, String fileName, String contentType,
                                long sizeBytes) {
        String id = ids.next();
        ClaimDocument document = new ClaimDocument(id, claimId, kind, fileName, contentType, sizeBytes,
                OffsetDateTime.now());
        documents.put(id, document);
        return document;
    }

    public List<ClaimDocument> findByClaim(String claimId, DocumentKind kind) {
        return documents.values().stream()
                .filter(d -> d.claimId().equals(claimId))
                .filter(d -> kind == null || d.kind() == kind)
                .sorted(Comparator.comparing(ClaimDocument::id))
                .toList();
    }

    public ClaimDocument findById(String claimId, String documentId) {
        ClaimDocument document = documents.get(documentId);
        if (document == null || !document.claimId().equals(claimId)) {
            throw new ResourceNotFoundException("ClaimDocument", documentId);
        }
        return document;
    }

    public DocumentUploadResult upload(String claimId, DocumentKind kind, String fileName,
                                       String contentType, long sizeBytes) {
        ClaimDocument document = store(claimId, kind, fileName, contentType, sizeBytes);
        String checksum = Integer.toHexString((fileName + sizeBytes).hashCode());
        return new DocumentUploadResult(document, "s3://claims-documents/" + claimId + "/" + document.id(),
                checksum);
    }

    public int size() {
        return documents.size();
    }
}
