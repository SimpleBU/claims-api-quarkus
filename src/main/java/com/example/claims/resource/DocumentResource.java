package com.example.claims.resource;

import com.example.claims.dto.ClaimDocument;
import com.example.claims.dto.DocumentKind;
import com.example.claims.dto.DocumentUploadResult;
import com.example.claims.service.DocumentService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.net.URI;
import java.util.List;

/**
 * Sub-resource of {@link ClaimResource}. The class carries no {@code @Path} of its own:
 * the prefix comes from the locator method that returns it.
 */
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {

    private final DocumentService documentService;
    private final String claimId;

    public DocumentResource(DocumentService documentService, String claimId) {
        this.documentService = documentService;
        this.claimId = claimId;
    }

    @GET
    public List<ClaimDocument> list(@QueryParam("kind") DocumentKind kind) {
        return documentService.findByClaim(claimId, kind);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@RestForm("kind") DocumentKind kind, @RestForm("file") FileUpload file) {
        DocumentKind resolved = kind == null ? DocumentKind.OTHER : kind;
        String fileName = file == null || file.fileName() == null ? "scan.bin" : file.fileName();
        String contentType = file == null || file.contentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : file.contentType();
        long size = file == null ? 0L : file.size();
        DocumentUploadResult result = documentService.upload(claimId, resolved, fileName, contentType, size);
        return Response.created(URI.create("/api/v1/claims/" + claimId + "/documents/"
                        + result.document().id()))
                .entity(result)
                .build();
    }
}
