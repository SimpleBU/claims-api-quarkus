package com.example.claims.service;

import com.example.claims.dto.AckResult;
import com.example.claims.dto.BankCallbackRequest;
import com.example.claims.dto.CacheSnapshot;
import com.example.claims.dto.PurgeResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class MaintenanceService {

    private final PolicyService policyService;
    private final ClaimService claimService;
    private final DocumentService documentService;
    private final PayoutService payoutService;
    private final Map<String, String> warmCache = new ConcurrentHashMap<>();
    private final AtomicLong lookups = new AtomicLong(4711);

    public MaintenanceService(PolicyService policyService, ClaimService claimService,
                              DocumentService documentService, PayoutService payoutService) {
        this.policyService = policyService;
        this.claimService = claimService;
        this.documentService = documentService;
        this.payoutService = payoutService;
        warmCache.put("reference:currencies", "3 entries");
        warmCache.put("reference:regions", "3 entries");
        warmCache.put("tariff:active", "4 entries");
    }

    public CacheSnapshot cacheSnapshot() {
        Map<String, Integer> regions = new LinkedHashMap<>();
        regions.put("policies", policyService.size());
        regions.put("claims", claimService.size());
        regions.put("documents", documentService.size());
        regions.put("payouts", payoutService.size());
        regions.put("warm", warmCache.size());
        lookups.incrementAndGet();
        return new CacheSnapshot(regions, lookups.get(), OffsetDateTime.now());
    }

    public PurgeResult purge(String key) {
        boolean removed = warmCache.remove(key) != null;
        return new PurgeResult(key, removed, warmCache.size());
    }

    public AckResult acceptBankCallback(BankCallbackRequest request) {
        String outcome = payoutService
                .applyBankCallback(request.externalReference(), request.status())
                .map(payout -> "APPLIED")
                .orElse("IGNORED");
        return new AckResult(outcome, request.externalReference(), OffsetDateTime.now());
    }
}
