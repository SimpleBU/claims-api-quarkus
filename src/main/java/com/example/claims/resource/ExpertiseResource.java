package com.example.claims.resource;

import com.example.claims.dto.Expertise;
import com.example.claims.dto.ExpertiseCreateRequest;
import com.example.claims.dto.PageResult;
import com.example.claims.dto.VerdictRequest;
import com.example.claims.dto.VerdictType;
import com.example.claims.service.ExpertiseService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.net.URI;
import java.util.List;

@Path("/v1/expertises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExpertiseResource {

    private final ExpertiseService expertiseService;

    public ExpertiseResource(ExpertiseService expertiseService) {
        this.expertiseService = expertiseService;
    }

    @GET
    public PageResult<Expertise> list(@QueryParam("claimId") String claimId,
                                      @QueryParam("verdict") VerdictType verdict,
                                      @QueryParam("offset") @DefaultValue("0") int offset,
                                      @QueryParam("limit") @DefaultValue("25") int limit) {
        List<Expertise> found = expertiseService.findAll(claimId, verdict);
        return PageResult.of(found, offset, limit);
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") String id) {
        Expertise expertise = expertiseService.findById(id);
        Response.Status status = expertise.verdict() == VerdictType.NEEDS_MORE_DATA
                ? Response.Status.PARTIAL_CONTENT
                : Response.Status.OK;
        return Response.status(status)
                .entity(expertise)
                .header("X-Expertise-Verdict", expertise.verdict().name())
                .build();
    }

    @POST
    public Response create(@Valid ExpertiseCreateRequest request) {
        Expertise expertise = expertiseService.create(request);
        return Response.status(Response.Status.CREATED)
                .location(URI.create("/api/v1/expertises/" + expertise.id()))
                .entity(expertise)
                .build();
    }

    @PATCH
    @Path("/{id}/verdict")
    public Expertise applyVerdict(@PathParam("id") String id, @Valid VerdictRequest request) {
        return expertiseService.applyVerdict(id, request);
    }

    @POST
    @Path("/{id}/report")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Expertise uploadReport(@PathParam("id") String id, @RestForm("file") FileUpload file) {
        String fileName = file == null || file.fileName() == null ? "report.pdf" : file.fileName();
        return expertiseService.attachReport(id, fileName);
    }

    @GET
    @Path("/{id}/report")
    @Produces("application/octet-stream")
    public Response downloadReport(@PathParam("id") String id) {
        byte[] payload = expertiseService.renderReport(id);
        return Response.ok(payload)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"expertise-" + id + ".txt\"")
                .header(HttpHeaders.CONTENT_LENGTH, payload.length)
                .build();
    }
}
