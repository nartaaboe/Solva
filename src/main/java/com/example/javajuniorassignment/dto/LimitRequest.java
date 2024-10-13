package com.example.javajuniorassignment.dto;

import java.math.BigDecimal;

public record LimitRequest(
        BigDecimal limitSum,
        String expenseCategory,
        String limitCurrencyShortname
) {
}
