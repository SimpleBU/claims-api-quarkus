package com.example.claims.service;

import com.example.claims.dto.Claim;
import com.example.claims.dto.ClaimCreateRequest;
import com.example.claims.dto.ClaimFilter;
import com.example.claims.dto.ClaimPatchRequest;
import com.example.claims.dto.ClaimStatus;
import com.example.claims.dto.ClaimType;
import com.example.claims.dto.MoneyAmount;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.ClaimNumberGenerator;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ClaimService {

    private final Map<String, Claim> claims = new ConcurrentHashMap<>();
    private final IdSequence ids = new IdSequence("clm", 900);
    private final ClaimNumberGenerator numbers = new ClaimNumberGenerator(70000);

    public ClaimService() {
        seed("pol-401", "cus-1", ClaimType.FIRE, ClaimStatus.EXPERTISE, "340000.00",
                "Kitchen fire caused by a faulty extension cord, smoke damage in two rooms");
        seed("pol-402", "cus-2", ClaimType.MEDICAL, ClaimStatus.UNDER_REVIEW, "78000.00",
                "Hospitalisation after a bicycle accident, three days of inpatient treatment");
        seed("pol-401", "cus-1", ClaimType.FLOOD, ClaimStatus.REGISTERED, "125000.00",
                "Water leak from the upstairs flat damaged the parquet floor and the wardrobe");
        seed("pol-403", "cus-3", ClaimType.THEFT, ClaimStatus.REJECTED, "56000.00",
                "Bicycle stolen from a locked staircase, police report attached to the claim file");
    }

    private void seed(String policyId, String customerId, ClaimType type, ClaimStatus status,
                      String amount, String description) {
        String id = ids.next();
        claims.put(id, new Claim(id, numbers.next(), policyId, customerId, type, status,
                LocalDate.now().minusDays(claims.size() + 3L), description, MoneyAmount.rub(amount), null,
                OffsetDateTime.now().minusDays(claims.size() + 2L), OffsetDateTime.now()));
    }

    public List<Claim> findAll(ClaimFilter filter) {
        return claims.values().stream()
                .filter(c -> filter.getStatus() == null || c.status() == filter.getStatus())
                .filter(c -> filter.getType() == null || c.type() == filter.getType())
                .filter(c -> filter.getPolicyId() == null || c.policyId().equals(filter.getPolicyId()))
                .sorted(Comparator.comparing(Claim::claimNumber))
                .toList();
    }

    public Claim findById(String id) {
        Claim claim = claims.get(id);
        if (claim == null) {
            throw new ResourceNotFoundException("Claim", id);
        }
        return claim;
    }

    public Claim findByNumber(String claimNumber) {
        return claims.values().stream()
                .filter(c -> c.claimNumber().equals(claimNumber))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Claim", claimNumber));
    }

    public Claim create(ClaimCreateRequest request) {
        String id = ids.next();
        Claim claim = new Claim(id, numbers.next(), request.policyId(), request.customerId(),
                request.type(), ClaimStatus.REGISTERED, request.incidentDate(), request.description(),
                request.claimedAmount(), null, OffsetDateTime.now(), OffsetDateTime.now());
        claims.put(id, claim);
        return claim;
    }

    public Claim patch(String id, ClaimPatchRequest request) {
        Claim claim = findById(id);
        if (request.status() != null) {
            claim = claim.withStatus(request.status());
        }
        if (request.approvedAmount() != null) {
            claim = claim.withApprovedAmount(request.approvedAmount());
        }
        if (request.description() != null) {
            claim = new Claim(claim.id(), claim.claimNumber(), claim.policyId(), claim.customerId(),
                    claim.type(), claim.status(), claim.incidentDate(), request.description(),
                    claim.claimedAmount(), claim.approvedAmount(), claim.registeredAt(), OffsetDateTime.now());
        }
        claims.put(id, claim);
        return claim;
    }

    public Claim reopen(String id, String reason) {
        Claim claim = findById(id);
        if (claim.status() != ClaimStatus.CLOSED && claim.status() != ClaimStatus.REJECTED) {
            throw new BusinessRuleException("CLAIM_NOT_CLOSED",
                    "Claim " + id + " is in status " + claim.status() + " and cannot be reopened");
        }
        Claim reopened = claim.withStatus(ClaimStatus.UNDER_REVIEW);
        claims.put(id, reopened);
        return reopened;
    }

    public boolean exists(String id) {
        return claims.containsKey(id);
    }

    public int size() {
        return claims.size();
    }
}
