package com.example.claims.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;

/**
 * Query and header parameters of the claim search endpoint, injected as a single
 * {@code @BeanParam} instead of a long method signature.
 */
public class ClaimFilter {

    @QueryParam("status")
    private ClaimStatus status;

    @QueryParam("type")
    private ClaimType type;

    @QueryParam("policyId")
    private String policyId;

    @QueryParam("offset")
    @DefaultValue("0")
    @Min(0)
    private int offset;

    @QueryParam("limit")
    @DefaultValue("25")
    @Min(1)
    @Max(200)
    private int limit;

    @HeaderParam("X-Tenant-Id")
    @DefaultValue("default")
    private String tenantId;

    public ClaimStatus getStatus() {
        return status;
    }

    public ClaimType getType() {
        return type;
    }

    public String getPolicyId() {
        return policyId;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public String getTenantId() {
        return tenantId;
    }
}
