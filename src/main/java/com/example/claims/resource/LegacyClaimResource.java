package com.example.claims.resource;

import com.example.claims.dto.Claim;
import com.example.claims.dto.ClaimFilter;
import com.example.claims.service.ClaimService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/v0/claims")
@Produces(MediaType.APPLICATION_JSON)
public class LegacyClaimResource {

    private final ClaimService claimService;

    public LegacyClaimResource(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GET
    public List<Claim> list() {
        return claimService.findAll(new ClaimFilter());
    }

    @GET
    @Path("/{id}")
    public Claim getOne(@PathParam("id") String id) {
        return claimService.findById(id);
    }
}
