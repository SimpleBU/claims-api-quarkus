package com.example.claims.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorrelationResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Object requestId = requestContext.getProperty(RequestIdFilter.REQUEST_ID);
        if (requestId != null) {
            responseContext.getHeaders().putSingle(RequestIdFilter.REQUEST_ID, requestId);
        }
        responseContext.getHeaders().putSingle("X-Service", "claims-api-quarkus");
    }
}
