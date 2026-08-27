package com.example.claims.resource;

import com.example.claims.dto.PageResult;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Read side of a catalogue resource. Concrete resources add the class level {@code @Path}
 * and the write operations.
 */
public abstract class AbstractCatalogResource<T> {

    protected abstract List<T> readAll();

    protected abstract T readOne(String id);

    protected abstract void removeOne(String id);

    @GET
    public PageResult<T> listAll(@QueryParam("offset") @DefaultValue("0") int offset,
                                 @QueryParam("limit") @DefaultValue("25") int limit) {
        return PageResult.of(readAll(), offset, limit);
    }

    @GET
    @Path("/{id}")
    public T getById(@PathParam("id") String id) {
        return readOne(id);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteById(@PathParam("id") String id) {
        removeOne(id);
        return Response.noContent().build();
    }
}
