package com.example.claims.service;

import com.example.claims.dto.ClaimType;
import com.example.claims.dto.MoneyAmount;
import com.example.claims.dto.Tariff;
import com.example.claims.dto.TariffCreateRequest;
import com.example.claims.dto.TariffUpdateRequest;
import com.example.claims.exception.ResourceNotFoundException;
import com.example.claims.model.IdSequence;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TariffService {

    private final Map<String, Tariff> tariffs = new ConcurrentHashMap<>();
    private final IdSequence ids = new IdSequence("trf", 10);

    public TariffService() {
        seed("TRF-FIRE01", "Fire and explosion base rate", ClaimType.FIRE, "0.018", "9000.00");
        seed("TRF-FLOOD1", "Water damage base rate", ClaimType.FLOOD, "0.012", "6500.00");
        seed("TRF-MED001", "Medical expenses base rate", ClaimType.MEDICAL, "0.026", "12000.00");
        seed("TRF-THEFT1", "Theft and burglary base rate", ClaimType.THEFT, "0.021", "7800.00");
    }

    private void seed(String code, String title, ClaimType appliesTo, String rate, String minimum) {
        String id = ids.next();
        tariffs.put(id, new Tariff(id, code, title, appliesTo, new BigDecimal(rate),
                MoneyAmount.rub(minimum), LocalDate.now().minusMonths(3), true));
    }

    public List<Tariff> findAll() {
        return tariffs.values().stream()
                .sorted(Comparator.comparing(Tariff::code))
                .toList();
    }

    public Tariff findById(String id) {
        Tariff tariff = tariffs.get(id);
        if (tariff == null) {
            throw new ResourceNotFoundException("Tariff", id);
        }
        return tariff;
    }

    public Tariff create(TariffCreateRequest request) {
        String id = ids.next();
        Tariff tariff = new Tariff(id, request.code(), request.title(), request.appliesTo(),
                request.baseRate(), request.minimumPremium(), request.effectiveFrom(), true);
        tariffs.put(id, tariff);
        return tariff;
    }

    public Tariff replace(String id, TariffUpdateRequest request) {
        Tariff current = findById(id);
        Tariff updated = new Tariff(id, current.code(), current.title(), current.appliesTo(),
                request.baseRate(), request.minimumPremium(), current.effectiveFrom(), request.active());
        tariffs.put(id, updated);
        return updated;
    }

    public void delete(String id) {
        if (tariffs.remove(id) == null) {
            throw new ResourceNotFoundException("Tariff", id);
        }
    }

    public int size() {
        return tariffs.size();
    }
}
