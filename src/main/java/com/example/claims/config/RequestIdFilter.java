package com.example.claims.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.UUID;

@Provider
public class RequestIdFilter implements ContainerRequestFilter {

    public static final String REQUEST_ID = "X-Request-Id";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String requestId = requestContext.getHeaderString(REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        requestContext.setProperty(REQUEST_ID, requestId);
    }
}
