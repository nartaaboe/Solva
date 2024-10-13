package com.example.javajuniorassignment.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        String accountFrom,
        String accountTo,
        String currencyShortname,
        BigDecimal sum,
        String expenseCategory
) {
}
