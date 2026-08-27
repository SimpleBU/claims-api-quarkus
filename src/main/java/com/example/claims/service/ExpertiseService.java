package com.example.claims.service;

import com.example.claims.dto.Expertise;
import com.example.claims.dto.ExpertiseCreateRequest;
import com.example.claims.dto.MoneyAmount;
import com.example.claims.dto.VerdictRequest;
import com.example.claims.dto.VerdictType;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ExpertiseService {

    private final Map<String, Expertise> expertises = new ConcurrentHashMap<>();
    private final IdSequence ids = new IdSequence("exp", 60);

    public ExpertiseService() {
        seed("clm-901", "Sergey Volkov", "NezavisimayaOcenka LLC", VerdictType.PARTIALLY_CONFIRMED,
                "290000.00");
        seed("clm-902", "Marina Belova", "MedExpert Group", VerdictType.CONFIRMED, "78000.00");
    }

    private void seed(String claimId, String expert, String organisation, VerdictType verdict,
                      String amount) {
        String id = ids.next();
        expertises.put(id, new Expertise(id, claimId, expert, organisation, LocalDate.now().minusDays(5),
                verdict, MoneyAmount.rub(amount), null, OffsetDateTime.now()));
    }

    public List<Expertise> findAll(String claimId, VerdictType verdict) {
        return expertises.values().stream()
                .filter(e -> claimId == null || e.claimId().equals(claimId))
                .filter(e -> verdict == null || e.verdict() == verdict)
                .sorted(Comparator.comparing(Expertise::id))
                .toList();
    }

    public Expertise findById(String id) {
        Expertise expertise = expertises.get(id);
        if (expertise == null) {
            throw new ResourceNotFoundException("Expertise", id);
        }
        return expertise;
    }

    public Expertise create(ExpertiseCreateRequest request) {
        String id = ids.next();
        Expertise expertise = new Expertise(id, request.claimId(), request.expertName(),
                request.organisation(), request.inspectionDate(), VerdictType.NEEDS_MORE_DATA, null, null,
                OffsetDateTime.now());
        expertises.put(id, expertise);
        return expertise;
    }

    public Expertise applyVerdict(String id, VerdictRequest request) {
        Expertise updated = findById(id).withVerdict(request.verdict(), request.assessedAmount());
        expertises.put(id, updated);
        return updated;
    }

    public Expertise attachReport(String id, String fileName) {
        Expertise updated = findById(id).withReport(fileName);
        expertises.put(id, updated);
        return updated;
    }

    public byte[] renderReport(String id) {
        Expertise expertise = findById(id);
        StringBuilder report = new StringBuilder();
        report.append("EXPERTISE REPORT\n");
        report.append("id: ").append(expertise.id()).append('\n');
        report.append("claim: ").append(expertise.claimId()).append('\n');
        report.append("expert: ").append(expertise.expertName()).append('\n');
        report.append("verdict: ").append(expertise.verdict()).append('\n');
        if (expertise.assessedAmount() != null) {
            report.append("assessed: ").append(expertise.assessedAmount().value()).append(' ')
                    .append(expertise.assessedAmount().currency()).append('\n');
        }
        return report.toString().getBytes(StandardCharsets.UTF_8);
    }

    public int size() {
        return expertises.size();
    }
}
