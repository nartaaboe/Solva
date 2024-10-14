package com.example.javajuniorassignment.service;

import com.example.javajuniorassignment.dto.LimitRequest;
import com.example.javajuniorassignment.dto.LimitResponse;
import com.example.javajuniorassignment.entity.Limit;
import com.example.javajuniorassignment.repository.LimitRepository;
import com.example.javajuniorassignment.service.impl.LimitServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LimitServiceImplTest {

    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private LimitServiceImpl limitService;

    private Limit limit;
    private LimitRequest limitRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        limit = Limit.builder()
                .id(1L)
                .limitSum(BigDecimal.valueOf(1000))
                .limitDateTime(LocalDateTime.now())
                .expirationDateTime(LocalDateTime.now().plusMonths(1))
                .expenseCategory("products")
                .limitCurrencyShortname("USD")
                .build();
        limitRequest = new LimitRequest(limit.getLimitSum(), limit.getExpenseCategory(), limit.getLimitCurrencyShortname());
    }

    @Test
    void testSetNewLimit_Success() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.empty());
        when(limitRepository.save(any(Limit.class))).thenReturn(limit);

        // Act
        LimitResponse response = limitService.setNewLimit(limitRequest);

        // Assert
        assertNotNull(response);
        assertEquals(limit.getId(), response.id());
        assertEquals(limit.getLimitSum(), response.limitSum());
        assertEquals(limit.getExpenseCategory(), response.expenseCategory());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
        verify(limitRepository, times(1)).save(any(Limit.class));
        verifyNoMoreInteractions(limitRepository);
    }

    @Test
    void testSetNewLimit_ActiveLimitExists() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.of(limit));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            limitService.setNewLimit(limitRequest);
        });

        assertEquals("Нельзя установить два лимита сразу.", exception.getMessage());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
        verifyNoMoreInteractions(limitRepository);
    }

    @Test
    void testGetCurrentLimit_Success() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.of(limit));

        // Act
        LimitResponse response = limitService.getCurrentLimit(anyString());

        // Assert
        assertNotNull(response);
        assertEquals(limit.getId(), response.id());
        assertEquals(limit.getLimitSum(), response.limitSum());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
        verifyNoMoreInteractions(limitRepository);
    }

    @Test
    void testGetCurrentLimit_NotFound() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            limitService.getCurrentLimit(anyString());
        });

        assertEquals("Лимит не найден.", exception.getMessage());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
        verifyNoMoreInteractions(limitRepository);
    }

    @Test
    void testRemoveLimit_NotFound() {
        // Arrange
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            limitService.removeLimit(anyString());
        });

        assertEquals("Лимит не найден.", exception.getMessage());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
        verifyNoMoreInteractions(limitRepository);
    }

    @Test
    void testRemoveLimit_Expired() {
        // Arrange
        limit.setExpirationDateTime(LocalDateTime.now().minusDays(1)); // Set to expired
        when(limitRepository.findLatestPushed(anyString())).thenReturn(Optional.of(limit));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            limitService.removeLimit(anyString());
        });

        assertEquals("Срок лимита истёк.", exception.getMessage());
        verify(limitRepository, times(1)).findLatestPushed(anyString());
        verifyNoMoreInteractions(limitRepository);
    }
}
