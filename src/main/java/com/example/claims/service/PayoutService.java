package com.example.claims.service;

import com.example.claims.dto.BankTransferDestination;
import com.example.claims.dto.CardDestination;
import com.example.claims.dto.MoneyAmount;
import com.example.claims.dto.Payout;
import com.example.claims.dto.PayoutApprovalRequest;
import com.example.claims.dto.PayoutCreateRequest;
import com.example.claims.dto.PayoutStatus;
import com.example.claims.dto.PayoutUpdateRequest;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.AuditRecord;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class PayoutService {

    private final Map<String, Payout> payouts = new ConcurrentHashMap<>();
    private final List<AuditRecord> auditTrail = new CopyOnWriteArrayList<>();
    private final IdSequence ids = new IdSequence("pay", 3100);

    public PayoutService() {
        String first = ids.next();
        payouts.put(first, new Payout(first, "clm-901", PayoutStatus.PENDING_APPROVAL,
                MoneyAmount.rub("290000.00"),
                new BankTransferDestination("40817810099910004312", "044525225", "Ivanov Ivan"),
                null, "bank-ref-" + first, OffsetDateTime.now()));
        String second = ids.next();
        payouts.put(second, new Payout(second, "clm-902", PayoutStatus.SETTLED, MoneyAmount.rub("78000.00"),
                new CardDestination("7711", "MIR", "RU"), "operator-12", "bank-ref-" + second,
                OffsetDateTime.now()));
    }

    public List<Payout> findAll(PayoutStatus status, String claimId) {
        return payouts.values().stream()
                .filter(p -> status == null || p.status() == status)
                .filter(p -> claimId == null || p.claimId().equals(claimId))
                .sorted(Comparator.comparing(Payout::id))
                .toList();
    }

    public Payout findById(String id) {
        Payout payout = payouts.get(id);
        if (payout == null) {
            throw new ResourceNotFoundException("Payout", id);
        }
        return payout;
    }

    public Payout create(PayoutCreateRequest request) {
        String id = ids.next();
        Payout payout = new Payout(id, request.claimId(), PayoutStatus.DRAFT, request.amount(),
                request.destination(), null, "bank-ref-" + id, OffsetDateTime.now());
        payouts.put(id, payout);
        return payout;
    }

    public Payout replace(String id, PayoutUpdateRequest request) {
        Payout payout = findById(id);
        if (payout.status() == PayoutStatus.SETTLED) {
            throw new BusinessRuleException("PAYOUT_SETTLED", "Settled payout " + id + " is immutable");
        }
        Payout updated = payout.withAmountAndDestination(request.amount(), request.destination());
        payouts.put(id, updated);
        return updated;
    }

    public Payout patchAmount(String id, MoneyAmount amount) {
        Payout payout = findById(id);
        Payout updated = payout.withAmountAndDestination(amount, payout.destination());
        payouts.put(id, updated);
        return updated;
    }

    public Payout approve(String id, PayoutApprovalRequest request) {
        Payout payout = findById(id);
        if (payout.status() != PayoutStatus.PENDING_APPROVAL && payout.status() != PayoutStatus.DRAFT) {
            throw new BusinessRuleException("PAYOUT_NOT_APPROVABLE",
                    "Payout " + id + " is in status " + payout.status());
        }
        Payout approved = payout.withStatus(PayoutStatus.APPROVED, request.approver());
        payouts.put(id, approved);
        auditTrail.add(AuditRecord.now(request.approver(), "PAYOUT_APPROVED", id));
        return approved;
    }

    public Optional<Payout> applyBankCallback(String externalReference, PayoutStatus status) {
        return payouts.values().stream()
                .filter(p -> externalReference.equals(p.externalReference()))
                .findFirst()
                .map(p -> {
                    Payout updated = p.withStatus(status, p.approvedBy());
                    payouts.put(p.id(), updated);
                    auditTrail.add(AuditRecord.now("bank", "CALLBACK_" + status, p.id()));
                    return updated;
                });
    }

    public List<AuditRecord> auditTrail() {
        return List.copyOf(auditTrail);
    }

    public int size() {
        return payouts.size();
    }
}
