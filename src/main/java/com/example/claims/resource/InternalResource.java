package com.example.claims.resource;

import com.example.claims.config.PURGE;
import com.example.claims.dto.AckResult;
import com.example.claims.dto.BankCallbackRequest;
import com.example.claims.dto.CacheSnapshot;
import com.example.claims.dto.PurgeResult;
import com.example.claims.service.MaintenanceService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/internal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InternalResource {

    private final MaintenanceService maintenanceService;

    public InternalResource(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GET
    @Path("/debug/cache")
    public CacheSnapshot cache() {
        return maintenanceService.cacheSnapshot();
    }

    @POST
    @Path("/webhooks/bank-callback")
    public Response bankCallback(@Valid BankCallbackRequest request) {
        AckResult ack = maintenanceService.acceptBankCallback(request);
        return Response.accepted(ack).build();
    }

    @PURGE
    @Path("/cache/{key}")
    public PurgeResult purge(@PathParam("key") String key) {
        return maintenanceService.purge(key);
    }
}
