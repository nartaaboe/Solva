package com.example.javajuniorassignment.repository;

import com.example.javajuniorassignment.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TransactionRepositoryTest {
    private TransactionRepository transactionRepository;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        MockitoAnnotations.openMocks(this);

        transactions = new ArrayList<>();
        transactions.add(Transaction.builder()
                .id(1L)
                .accountFrom("AccountA")
                .accountTo("AccountB")
                .currencyShortname("USD")
                .sum(BigDecimal.valueOf(200))
                .sumInUSD(BigDecimal.valueOf(200))
                .expenseCategory("products")
                .transactionDateTime(LocalDateTime.now())
                .limitExceeded(true)
                .build());
        transactions.add(Transaction.builder()
                .id(2L)
                .accountFrom("AccountC")
                .accountTo("AccountD")
                .currencyShortname("USD")
                .sum(BigDecimal.valueOf(150))
                .sumInUSD(BigDecimal.valueOf(150))
                .expenseCategory("services")
                .transactionDateTime(LocalDateTime.now().minusDays(1))
                .limitExceeded(false)
                .build());
    }

    @Test
    void testFindAll() {
        // Arrange
        Page<Transaction> transactionPage = new PageImpl<>(transactions);
        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(transactionPage);

        // Act
        Page<Transaction> result = transactionRepository.findAll(Pageable.unpaged());

        // Assert
        assertEquals(2, result.getContent().size());
        verify(transactionRepository, times(1)).findAll(any(Pageable.class));
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void testFindByLimitExceeded() {
        // Arrange
        when(transactionRepository.findByLimitExceeded(true)).thenReturn(List.of(transactions.get(0))); // Only return the first transaction

        // Act
        List<Transaction> result = transactionRepository.findByLimitExceeded(true);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isLimitExceeded());
        verify(transactionRepository, times(1)).findByLimitExceeded(true);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void testFindByRange() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now();
        when(transactionRepository.findByRange(start, end)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionRepository.findByRange(start, end);

        // Assert
        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByRange(start, end);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void testFindByLimitExceededAndExpenseCategory() {
        // Arrange
        when(transactionRepository.findByLimitExceededAndExpenseCategory(anyBoolean(), anyString()))
                .thenReturn(List.of(transactions.get(0))); // Return the first transaction

        // Act
        List<Transaction> result = transactionRepository.findByLimitExceededAndExpenseCategory(anyBoolean(), anyString());

        // Assert
        assertEquals(1, result.size());
        assertEquals(transactions.get(0).getExpenseCategory(), result.get(0).getExpenseCategory());
        verify(transactionRepository, times(1)).findByLimitExceededAndExpenseCategory(anyBoolean(), anyString());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void testFindByLimitExceededAndExpenseCategory_NotFound() {
        // Arrange
        when(transactionRepository.findByLimitExceededAndExpenseCategory(anyBoolean(), anyString())).thenReturn(new ArrayList<>());

        // Act
        List<Transaction> result = transactionRepository.findByLimitExceededAndExpenseCategory(anyBoolean(), anyString());

        // Assert
        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).findByLimitExceededAndExpenseCategory(anyBoolean(), anyString());
        verifyNoMoreInteractions(transactionRepository);
    }
}
