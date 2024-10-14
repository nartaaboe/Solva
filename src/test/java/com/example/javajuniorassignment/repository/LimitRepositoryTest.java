package com.example.javajuniorassignment.repository;

import com.example.javajuniorassignment.entity.Limit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LimitRepositoryTest {
    private LimitRepository limitRepository;
    private Limit limit;

    @BeforeEach
    void setUp() {
        limitRepository = mock(LimitRepository.class);
        MockitoAnnotations.openMocks(this);

        limit = Limit.builder()
                .id(1L)
                .limitSum(BigDecimal.valueOf(1500.00))
                .limitDateTime(LocalDateTime.now())
                .expirationDateTime(LocalDateTime.now().plusDays(30))
                .expenseCategory("products")
                .limitCurrencyShortname("USD")
                .build();
    }

    @Test
    void testFindLatestPushed() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.of(limit));

        // Act
        Optional<Limit> result = limitRepository.findLatestPushed(anyString());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(limit.getExpenseCategory(), result.get().getExpenseCategory());
        assertEquals(limit.getLimitSum(), result.get().getLimitSum());
        assertEquals(limit.getLimitCurrencyShortname(), result.get().getLimitCurrencyShortname());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
    }

    @Test
    void testFindLatestPushed_NotFound() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<Limit> result = limitRepository.findLatestPushed(anyString());

        // Assert
        assertTrue(result.isEmpty());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
    }
}
