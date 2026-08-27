package com.example.claims.resource;

import com.example.claims.dto.ReferenceItem;
import com.example.claims.service.ReferenceService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * The class itself is mounted at the application root; every path is declared on the methods.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ReferenceResource {

    private final ReferenceService referenceService;

    public ReferenceResource(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GET
    @Path("/v1/reference/currencies")
    public List<ReferenceItem> currencies() {
        return referenceService.currencies();
    }

    @GET
    @Path("/v1/reference/claim-types")
    public List<ReferenceItem> claimTypes() {
        return referenceService.claimTypes();
    }

    @GET
    @Path("/v1/reference/regions")
    public List<ReferenceItem> regions() {
        return referenceService.regions();
    }
}
