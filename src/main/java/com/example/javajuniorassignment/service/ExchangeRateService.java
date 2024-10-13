package com.example.javajuniorassignment.service;

import com.example.javajuniorassignment.dto.ExchangeRateResponse;

import java.math.BigDecimal;


public interface ExchangeRateService {
    ExchangeRateResponse getBySymbol(String symbol);
    BigDecimal convertToUSD(String currency, BigDecimal amount);
}
