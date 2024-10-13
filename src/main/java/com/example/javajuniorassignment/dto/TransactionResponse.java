package com.example.javajuniorassignment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String accountFrom,
        String accountTo,
        String currencyShortName,
        BigDecimal sum,
        BigDecimal sumInUSD,
        String expenseCategory,
        LocalDateTime transactionDateTime,
        boolean limitExceeded
) {
}
