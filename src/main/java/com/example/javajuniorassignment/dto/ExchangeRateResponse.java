package com.example.javajuniorassignment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateResponse(
        Long id,
        String currencyFrom,
        String currencyTo,
        BigDecimal rate,
        LocalDateTime dateTime
) {
}
