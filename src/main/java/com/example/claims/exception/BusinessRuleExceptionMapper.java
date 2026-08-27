package com.example.claims.exception;

import com.example.claims.dto.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.util.List;

@Provider
public class BusinessRuleExceptionMapper implements ExceptionMapper<BusinessRuleException> {

    @Override
    public Response toResponse(BusinessRuleException exception) {
        ErrorResponse body = new ErrorResponse(exception.getCode(), exception.getMessage(),
                null, OffsetDateTime.now(), List.of());
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(body)
                .build();
    }
}
