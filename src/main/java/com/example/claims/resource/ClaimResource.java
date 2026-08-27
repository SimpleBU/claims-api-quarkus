package com.example.claims.resource;

import com.example.claims.dto.Claim;
import com.example.claims.dto.ClaimCreateRequest;
import com.example.claims.dto.ClaimFilter;
import com.example.claims.dto.ClaimPatchRequest;
import com.example.claims.dto.PageResult;
import com.example.claims.service.ClaimService;
import com.example.claims.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/v1/claims")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClaimResource {

    private final ClaimService claimService;
    private final DocumentService documentService;

    public ClaimResource(ClaimService claimService, DocumentService documentService) {
        this.claimService = claimService;
        this.documentService = documentService;
    }

    @GET
    public PageResult<Claim> search(@BeanParam ClaimFilter filter) {
        List<Claim> found = claimService.findAll(filter);
        return PageResult.of(found, filter.getOffset(), filter.getLimit());
    }

    @GET
    @Path("/{id}")
    public Claim getOne(@PathParam("id") String id) {
        return claimService.findById(id);
    }

    @GET
    @Path("/by-number/{claimNumber: CLM-[0-9]{8}}")
    public Claim getByNumber(@PathParam("claimNumber") String claimNumber) {
        return claimService.findByNumber(claimNumber);
    }

    @POST
    public Response register(@Valid ClaimCreateRequest request) {
        Claim claim = claimService.create(request);
        return Response.created(URI.create("/api/v1/claims/" + claim.id()))
                .entity(claim)
                .build();
    }

    @PATCH
    @Path("/{id}")
    public Claim patch(@PathParam("id") String id, @Valid ClaimPatchRequest request) {
        return claimService.patch(id, request);
    }

    @POST
    @Path("/{id}/reopen")
    public Claim reopen(@PathParam("id") String id, @QueryParam("reason") String reason) {
        return claimService.reopen(id, reason);
    }

    @Path("/{claimId}/documents")
    public DocumentResource documents(@PathParam("claimId") String claimId) {
        if (!claimService.exists(claimId)) {
            throw new com.example.claims.exception.ResourceNotFoundException("Claim", claimId);
        }
        return new DocumentResource(documentService, claimId);
    }
}
