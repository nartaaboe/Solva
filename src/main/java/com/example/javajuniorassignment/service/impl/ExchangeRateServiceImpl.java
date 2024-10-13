package com.example.javajuniorassignment.service.impl;

import com.example.javajuniorassignment.dto.ExchangeRateResponse;
import com.example.javajuniorassignment.entity.ExchangeRate;
import com.example.javajuniorassignment.repository.ExchangeRateRepository;
import com.example.javajuniorassignment.service.ExchangeRateService;
import com.example.javajuniorassignment.util.ExchangeRateClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateClient exchangeRateClient;

    @Override
    public ExchangeRateResponse getBySymbol(String symbol) {
        log.info("Запрос курса валюты для символа: {}", symbol);
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findLatestPushed(symbol);
        if (!exchangeRate.isPresent()) {
            log.error("Курс валюты для символа {} не найден.", symbol);
            throw new EntityNotFoundException("Курс валют не найден.");
        }
        log.info("Найден курс валюты: {}", exchangeRate.get());
        return mapToResponse(exchangeRate.get());
    }

    /**
     * Конвертирует заданную сумму в указанной валюте в USD.
     * <p>
     * Если курс валюты не найден, выбрасывается исключение {@link EntityNotFoundException}.
     * Перед выполнением конвертации обновляются актуальные курсы.
     * </p>
     *
     * @param currency код валюты (например, "KZT").
     * @param amount сумма для конвертации.
     * @return сумма в USD.
     * @throws EntityNotFoundException если курс валют не найден.
     */
    @Override
    public BigDecimal convertToUSD(String currency, BigDecimal amount) {
        log.info("Конвертация суммы {} {} в USD.", amount, currency);
//        updateExchangeRate();
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findLatestPushed(currency + "/" + "USD");
        if (!exchangeRate.isPresent()) {
            log.error("Курс валюты {} не найден для конвертации в USD.", currency);
            throw new EntityNotFoundException("Курс валют не найден.");
        }
        BigDecimal rate = exchangeRate.get().getRate();
        BigDecimal convertedAmount = amount.multiply(rate);
        log.info("Конвертированная сумма: {} USD по курсу {}.", convertedAmount, rate);
        return convertedAmount;
    }

    /**
     * Периодически обновляет курсы валют с помощью внешнего API.
     * <p>
     * Метод выполняется по расписанию каждый день в 10:00 утра.
     * Если курсы валют не доступны, выбрасывается {@link RuntimeException}.
     * </p>
     */
    @Scheduled(cron = "0 0 10 * * ?")
    private void updateExchangeRate() {
        log.info("Запуск обновления курсов валют...");
        Mono<Map<String, ExchangeRate>> ratesMono = exchangeRateClient.getLastExchangeRates(
                List.of("USD/KZT", "USD/RUB", "RUB/KZT", "RUB/USD", "KZT/USD", "KZT/RUB"));

        ratesMono.subscribe(rates -> {
            if (rates == null || rates.isEmpty()) {
                log.error("Курсы валют не доступны на twelve data.");
                throw new RuntimeException("Курс валют не доступны на twelve data.");
            }
            rates.forEach((key, exchangeRate) -> {
                exchangeRate.setDateTime(new Timestamp(exchangeRate.getTimestamp()).toLocalDateTime());
                log.info("Сохраняем курс валюты: {} с курсом {}", exchangeRate.getSymbol(), exchangeRate.getRate());
                exchangeRateRepository.save(exchangeRate);
            });
            log.info("Обновление курсов валют завершено.");
        }, error -> log.error("Ошибка при получении курсов валют: {}", error.getMessage()));
    }

    private ExchangeRateResponse mapToResponse(ExchangeRate exchangeRate) {
        String[] symbols = exchangeRate.getSymbol().split("/");
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                symbols[0],
                symbols[1],
                exchangeRate.getRate(),
                exchangeRate.getDateTime()
        );
    }
}