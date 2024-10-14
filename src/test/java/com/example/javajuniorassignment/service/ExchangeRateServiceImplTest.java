package com.example.javajuniorassignment.service;

import com.example.javajuniorassignment.dto.ExchangeRateResponse;
import com.example.javajuniorassignment.entity.ExchangeRate;
import com.example.javajuniorassignment.repository.ExchangeRateRepository;
import com.example.javajuniorassignment.service.impl.ExchangeRateServiceImpl;
import com.example.javajuniorassignment.util.ExchangeRateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateServiceImplTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private ExchangeRate exchangeRate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exchangeRate = ExchangeRate.builder()
                .id(1L)
                .symbol("USD/KZT")
                .rate(BigDecimal.valueOf(460.00))
                .timestamp(System.currentTimeMillis())
                .dateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetBySymbol_Success() {
        // Arrange
        when(exchangeRateRepository.findLatestPushed("USD/KZT")).thenReturn(Optional.of(exchangeRate));

        // Act
        ExchangeRateResponse response = exchangeRateService.getBySymbol("USD/KZT");

        // Assert
        assertNotNull(response);
        assertEquals("USD", response.currencyFrom());
        assertEquals("KZT", response.currencyTo());
        assertEquals(exchangeRate.getRate(), response.rate());
        assertEquals(exchangeRate.getDateTime(), response.dateTime());
        verify(exchangeRateRepository, times(1)).findLatestPushed("USD/KZT");
    }

    @Test
    void testGetBySymbol_NotFound() {
        // Arrange
        when(exchangeRateRepository.findLatestPushed("USD/KZT")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            exchangeRateService.getBySymbol("USD/KZT");
        });

        assertEquals("Курс валют не найден.", exception.getMessage());
        verify(exchangeRateRepository, times(1)).findLatestPushed("USD/KZT");
    }

    @Test
    void testConvertToUSD_Success() {
        // Arrange
        when(exchangeRateRepository.findLatestPushed(anyString())).thenReturn(Optional.of(exchangeRate));

        // Act
        BigDecimal convertedAmount = exchangeRateService.convertToUSD(anyString(), BigDecimal.valueOf(anyDouble()));

        // Assert
        assertNotNull(convertedAmount);
        assertEquals(exchangeRate., convertedAmount);
        verify(exchangeRateRepository, times(1)).findLatestPushed("KZT/USD");
    }

    @Test
    void testConvertToUSD_NotFound() {
        // Arrange
        when(exchangeRateRepository.findLatestPushed("KZT/USD")).thenReturn(Optional.empty());
        BigDecimal amount = BigDecimal.valueOf(1000);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            exchangeRateService.convertToUSD("KZT", amount);
        });

        assertEquals("Курс валют не найден.", exception.getMessage());
        verify(exchangeRateRepository, times(1)).findLatestPushed("KZT/USD");
    }
}
