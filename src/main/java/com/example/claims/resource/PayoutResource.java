package com.example.claims.resource;

import com.example.claims.dto.MoneyAmount;
import com.example.claims.dto.PageResult;
import com.example.claims.dto.Payout;
import com.example.claims.dto.PayoutApprovalRequest;
import com.example.claims.dto.PayoutCreateRequest;
import com.example.claims.dto.PayoutStatus;
import com.example.claims.dto.PayoutUpdateRequest;
import com.example.claims.service.PayoutService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path(PayoutResource.BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PayoutResource {

    public static final String BASE = "/v1/payouts";

    private final PayoutService payoutService;

    public PayoutResource(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @GET
    public PageResult<Payout> list(@QueryParam("status") PayoutStatus status,
                                   @QueryParam("claimId") String claimId,
                                   @QueryParam("offset") @DefaultValue("0") int offset,
                                   @QueryParam("limit") @DefaultValue("25") int limit) {
        List<Payout> found = payoutService.findAll(status, claimId);
        return PageResult.of(found, offset, limit);
    }

    @GET
    @Path("/{id}")
    public Payout getOne(@PathParam("id") String id) {
        return payoutService.findById(id);
    }

    @POST
    public Response create(@Valid PayoutCreateRequest request) {
        Payout payout = payoutService.create(request);
        return Response.created(URI.create("/api" + BASE + "/" + payout.id()))
                .entity(payout)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Payout replace(@PathParam("id") String id, @Valid PayoutUpdateRequest request) {
        return payoutService.replace(id, request);
    }

    @PATCH
    @Path("/{id}")
    public Payout patchAmount(@PathParam("id") String id, @Valid MoneyAmount amount) {
        return payoutService.patchAmount(id, amount);
    }

    @POST
    @Path("/{id}/approve")
    public Payout approve(@PathParam("id") String id, @Valid PayoutApprovalRequest request) {
        return payoutService.approve(id, request);
    }
}
