package com.example.claims.resource;

import com.example.claims.dto.PageResult;
import com.example.claims.dto.Policy;
import com.example.claims.dto.PolicyCreateRequest;
import com.example.claims.dto.PolicyStatus;
import com.example.claims.dto.PolicyUpdateRequest;
import com.example.claims.service.PolicyService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/v1/policies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PolicyResource {

    private final PolicyService policyService;

    public PolicyResource(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GET
    public PageResult<Policy> list(@QueryParam("status") PolicyStatus status,
                                   @QueryParam("holderId") String holderId,
                                   @QueryParam("offset") @DefaultValue("0") int offset,
                                   @QueryParam("limit") @DefaultValue("25") int limit) {
        List<Policy> found = policyService.findAll(status, holderId);
        return PageResult.of(found, offset, limit);
    }

    @GET
    @Path("/{id}")
    public Policy getOne(@PathParam("id") String id) {
        return policyService.findById(id);
    }

    @POST
    public Response create(@Valid PolicyCreateRequest request) {
        Policy policy = policyService.create(request);
        return Response.created(URI.create("/api/v1/policies/" + policy.id()))
                .entity(policy)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Policy replace(@PathParam("id") String id, @Valid PolicyUpdateRequest request) {
        return policyService.replace(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        policyService.delete(id);
        return Response.noContent().build();
    }

    @HEAD
    @Path("/{id}/status")
    public Response probeStatus(@PathParam("id") String id) {
        return policyService.findOptional(id)
                .map(policy -> Response.noContent().header("X-Policy-Status", policy.status().name()).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @OPTIONS
    public Response describe() {
        return Response.ok()
                .header(HttpHeaders.ALLOW, "GET,POST,PUT,DELETE,HEAD,OPTIONS")
                .header("X-Policy-Statuses", "DRAFT,ACTIVE,SUSPENDED,EXPIRED,TERMINATED")
                .build();
    }
}
