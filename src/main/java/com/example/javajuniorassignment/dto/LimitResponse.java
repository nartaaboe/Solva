package com.example.javajuniorassignment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LimitResponse(
        Long id,
        BigDecimal limitSum,
        LocalDateTime limitDateTime,
        LocalDateTime expirationDateTime,
        String expenseCategory,
        String limitCurrencyShortname
) {
}
