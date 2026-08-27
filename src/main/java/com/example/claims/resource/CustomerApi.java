package com.example.claims.resource;

import com.example.claims.dto.ContactPatchRequest;
import com.example.claims.dto.Customer;
import com.example.claims.dto.CustomerCreateRequest;
import com.example.claims.dto.CustomerUpdateRequest;
import com.example.claims.dto.PageResult;
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

/**
 * The whole HTTP contract of the customer resource lives on this interface;
 * the implementing class carries no JAX-RS annotations at all.
 */
@Path("/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CustomerApi {

    @GET
    PageResult<Customer> list(@QueryParam("lastName") String lastName,
                              @QueryParam("offset") @DefaultValue("0") int offset,
                              @QueryParam("limit") @DefaultValue("25") int limit);

    @GET
    @Path("/{id}")
    Customer getOne(@PathParam("id") String id);

    @POST
    Response create(@Valid CustomerCreateRequest request);

    @PUT
    @Path("/{id}")
    Customer replace(@PathParam("id") String id, @Valid CustomerUpdateRequest request);

    @PATCH
    @Path("/{id}/contacts")
    Customer patchContacts(@PathParam("id") String id, @Valid ContactPatchRequest request);
}
