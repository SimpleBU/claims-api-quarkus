package com.example.claims.resource;

import com.example.claims.dto.Tariff;
import com.example.claims.dto.TariffCreateRequest;
import com.example.claims.dto.TariffUpdateRequest;
import com.example.claims.service.TariffService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/v1/tariffs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TariffResource extends AbstractCatalogResource<Tariff> {

    private final TariffService tariffService;

    public TariffResource(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Override
    protected List<Tariff> readAll() {
        return tariffService.findAll();
    }

    @Override
    protected Tariff readOne(String id) {
        return tariffService.findById(id);
    }

    @Override
    protected void removeOne(String id) {
        tariffService.delete(id);
    }

    @POST
    public Response create(@Valid TariffCreateRequest request) {
        Tariff tariff = tariffService.create(request);
        return Response.created(URI.create("/api/v1/tariffs/" + tariff.id()))
                .entity(tariff)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Tariff replace(@PathParam("id") String id, @Valid TariffUpdateRequest request) {
        return tariffService.replace(id, request);
    }
}
