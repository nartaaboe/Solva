package com.example.javajuniorassignment.service.impl;

import com.example.javajuniorassignment.dto.LimitRequest;
import com.example.javajuniorassignment.dto.LimitResponse;
import com.example.javajuniorassignment.entity.Limit;
import com.example.javajuniorassignment.repository.LimitRepository;
import com.example.javajuniorassignment.service.LimitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;

    /**
     * Устанавливает новый лимит на расходы.
     *
     * @param limitRequest объект запроса с параметрами лимита.
     * @return {@link LimitResponse} с информацией о созданном лимите.
     * @throws IllegalArgumentException если сумма лимита неположительна или не указана категория.
     * @throws RuntimeException если уже существует активный лимит для указанной категории.
     */
    @Override
    public LimitResponse setNewLimit(LimitRequest limitRequest) {
        if (limitRequest.limitSum().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Сумма лимита должна быть положительной.");
            throw new IllegalArgumentException("Сумма лимита должна быть положительной.");
        }
        if (limitRequest.expenseCategory() == null) {
            log.error("Нужно указать категорию лимита.");
            throw new IllegalArgumentException("Нужно указать категорию лимита.");
        }

        Limit latest = limitRepository.findLatestPushed(limitRequest.expenseCategory()).orElse(null);
        if (latest != null && latest.getExpirationDateTime().isAfter(LocalDateTime.now())) {
            log.error("Нельзя установить два лимита сразу для категории: {}", limitRequest.expenseCategory());
            throw new RuntimeException("Нельзя установить два лимита сразу.");
        }

        Limit limit = limitRepository.save(toEntity(limitRequest));
        log.info("Новый лимит установлен: {}", limit);
        return mapToResponse(limit);
    }

    /**
     * Возвращает текущий активный лимит для указанной категории расходов.
     *
     * @param expenseCategory категория расходов.
     * @return {@link LimitResponse} с данными текущего лимита.
     * @throws EntityNotFoundException если лимит не найден или его срок истёк.
     */
    @Override
    public LimitResponse getCurrentLimit(String expenseCategory) {
        Limit limit = limitRepository.findLatestPushed(expenseCategory)
                .orElseThrow(() -> new EntityNotFoundException("Лимит не найден."));
        log.info("Текущий лимит для категории {}: {}", expenseCategory, limit.getLimitSum());

        if (limit.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            log.error("Срок лимита истёк для категории: {}", expenseCategory);
            throw new EntityNotFoundException("Срок лимита истёк.");
        }
        return mapToResponse(limit);
    }

    /**
     * Удаляет (деактивирует) лимит для указанной категории расходов.
     * <p>Устанавливает время истечения лимита на текущее время.</p>
     *
     * @param expenseCategory категория расходов.
     * @return {@link LimitResponse} с информацией об удалённом лимите.
     * @throws EntityNotFoundException если лимит не найден или уже истёк.
     */
    @Override
    public LimitResponse removeLimit(String expenseCategory) {
        Limit limit = limitRepository.findLatestPushed(expenseCategory)
                .orElseThrow(() -> new EntityNotFoundException("Лимит не найден."));

        if (limit.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            log.error("Срок лимита истёк для категории: {}", expenseCategory);
            throw new EntityNotFoundException("Срок лимита истёк.");
        }

        limit.setExpirationDateTime(LocalDateTime.now());
        Limit savedLimit = limitRepository.save(limit);
        log.info("Лимит удалён для категории: {}", expenseCategory);
        return mapToResponse(savedLimit);
    }

    private LimitResponse mapToResponse(Limit limit) {
        return new LimitResponse(
                limit.getId(),
                limit.getLimitSum(),
                limit.getLimitDateTime(),
                limit.getExpirationDateTime(),
                limit.getExpenseCategory(),
                limit.getLimitCurrencyShortname()
        );
    }

    private Limit toEntity(LimitRequest limitRequest) {
        return Limit.builder()
                .limitSum(limitRequest.limitSum())
                .limitDateTime(LocalDateTime.now())
                .expirationDateTime(LocalDateTime.now().plusMonths(1))
                .expenseCategory(limitRequest.expenseCategory())
                .limitCurrencyShortname(limitRequest.limitCurrencyShortname())
                .build();
    }
}
