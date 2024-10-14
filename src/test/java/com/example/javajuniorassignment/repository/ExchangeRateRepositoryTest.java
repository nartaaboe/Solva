package com.example.javajuniorassignment.repository;

import com.example.javajuniorassignment.entity.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExchangeRateRepositoryTest {
    private ExchangeRateRepository exchangeRateRepository;

    @BeforeEach
    void setUp() {
        exchangeRateRepository = mock(ExchangeRateRepository.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        exchangeRates.add(new ExchangeRate(1L, "USD/KZT", BigDecimal.valueOf(430.50)));
        exchangeRates.add(new ExchangeRate(2L, "EUR/KZT", BigDecimal.valueOf(500.75)));
        Page<ExchangeRate> page = new PageImpl<>(exchangeRates);

        when(exchangeRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<ExchangeRate> result = exchangeRateRepository.findAll(Pageable.unpaged());

        // Assert
        assertEquals(2, result.getContent().size());
        verify(exchangeRateRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindLatestPushed() {
        // Arrange
        ExchangeRate exchangeRate = new ExchangeRate(3L, "USD/KZT", BigDecimal.valueOf(435.00));
        when(exchangeRateRepository.findLatestPushed(anyString())).thenReturn(Optional.of(exchangeRate));

        // Act
        Optional<ExchangeRate> result = exchangeRateRepository.findLatestPushed(anyString());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(exchangeRate.getSymbol(), result.get().getSymbol());
        assertEquals(exchangeRate.getRate(), result.get().getRate());
        verify(exchangeRateRepository, times(1)).findLatestPushed(anyString());
    }

    @Test
    void testFindLatestPushed_NotFound() {
        // Arrange
        when(exchangeRateRepository.findLatestPushed(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<ExchangeRate> result = exchangeRateRepository.findLatestPushed(anyString());

        // Assert
        assertTrue(result.isEmpty());
        verify(exchangeRateRepository, times(1)).findLatestPushed(anyString());
    }
}
