package com.example.claims.service;

import com.example.claims.dto.Coverage;
import com.example.claims.dto.MoneyAmount;
import com.example.claims.dto.Policy;
import com.example.claims.dto.PolicyCreateRequest;
import com.example.claims.dto.PolicyStatus;
import com.example.claims.dto.PolicyUpdateRequest;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PolicyService {

    private final Map<String, Policy> policies = new ConcurrentHashMap<>();
    private final IdSequence ids = new IdSequence("pol", 400);

    public PolicyService() {
        seed("POL-0000000401", "cus-1", PolicyStatus.ACTIVE, "48000.00",
                List.of(new Coverage("PROP", "Property damage", MoneyAmount.rub("3000000.00"),
                                MoneyAmount.rub("15000.00")),
                        new Coverage("FIRE", "Fire and explosion", MoneyAmount.rub("5000000.00"),
                                MoneyAmount.rub("25000.00"))));
        seed("POL-0000000402", "cus-2", PolicyStatus.ACTIVE, "12500.00",
                List.of(new Coverage("MED", "Medical expenses", MoneyAmount.rub("900000.00"),
                        MoneyAmount.rub("5000.00"))));
        seed("POL-0000000403", "cus-3", PolicyStatus.EXPIRED, "9900.00",
                List.of(new Coverage("THEFT", "Theft and burglary", MoneyAmount.rub("700000.00"),
                        MoneyAmount.rub("10000.00"))));
    }

    private void seed(String number, String holderId, PolicyStatus status, String premium,
                      List<Coverage> coverages) {
        String id = ids.next();
        policies.put(id, new Policy(id, number, holderId, status, LocalDate.now().minusMonths(6),
                LocalDate.now().plusMonths(6), MoneyAmount.rub(premium), coverages, OffsetDateTime.now()));
    }

    public List<Policy> findAll(PolicyStatus status, String holderId) {
        return policies.values().stream()
                .filter(p -> status == null || p.status() == status)
                .filter(p -> holderId == null || p.holderId().equals(holderId))
                .sorted(Comparator.comparing(Policy::number))
                .toList();
    }

    public Policy findById(String id) {
        Policy policy = policies.get(id);
        if (policy == null) {
            throw new ResourceNotFoundException("Policy", id);
        }
        return policy;
    }

    public Optional<Policy> findOptional(String id) {
        return Optional.ofNullable(policies.get(id));
    }

    public Policy create(PolicyCreateRequest request) {
        String id = ids.next();
        Policy policy = new Policy(id, request.number(), request.holderId(), PolicyStatus.DRAFT,
                request.validFrom(), request.validTo(), request.premium(),
                List.copyOf(request.coverages()), OffsetDateTime.now());
        policies.put(id, policy);
        return policy;
    }

    public Policy replace(String id, PolicyUpdateRequest request) {
        Policy current = findById(id);
        Policy updated = new Policy(id, current.number(), current.holderId(), request.status(),
                current.validFrom(), request.validTo(), request.premium(),
                request.coverages() == null ? current.coverages() : List.copyOf(request.coverages()),
                OffsetDateTime.now());
        policies.put(id, updated);
        return updated;
    }

    public void delete(String id) {
        if (policies.remove(id) == null) {
            throw new ResourceNotFoundException("Policy", id);
        }
    }

    public int size() {
        return policies.size();
    }
}
