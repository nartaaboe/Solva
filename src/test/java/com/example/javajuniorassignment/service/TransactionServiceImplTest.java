package com.example.javajuniorassignment.service;

import com.example.javajuniorassignment.dto.LimitResponse;
import com.example.javajuniorassignment.dto.TransactionRequest;
import com.example.javajuniorassignment.dto.TransactionResponse;
import com.example.javajuniorassignment.entity.Transaction;
import com.example.javajuniorassignment.repository.TransactionRepository;
import com.example.javajuniorassignment.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LimitService limitService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        // Инициализация объекта Transaction для реиспользования
        transaction = new Transaction();
        transaction.setAccountFrom("12345");
        transaction.setAccountTo("67890");
        transaction.setCurrencyShortname("KZT");
        transaction.setSum(new BigDecimal("100000.0"));
        transaction.setSumInUSD(new BigDecimal("200.0"));
        transaction.setExpenseCategory("products");
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setLimitExceeded(false);

        // Инициализация объекта TransactionRequest
        transactionRequest = new TransactionRequest(
                transaction.getAccountFrom(),
                transaction.getAccountTo(),
                transaction.getCurrencyShortname(),
                transaction.getSum(),
                transaction.getExpenseCategory()
        );
    }

    @Test
    void testCreateTransaction() {
        // arrange
        LimitResponse limitResponse = new LimitResponse(
                1L,
                new BigDecimal("500.00"),
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1),
                transaction.getExpenseCategory(),
                "USD"
        );

        when(limitService.getCurrentLimit(any())).thenReturn(limitResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(exchangeRateService.convertToUSD(any(), any())).thenReturn(new BigDecimal("200.0"));

        // act
        TransactionResponse response = transactionService.createTransaction(transactionRequest);

        // assert
        assertNotNull(response);
        assertEquals(transaction.getAccountFrom(), response.accountFrom());
        assertEquals(transaction.getAccountTo(), response.accountTo());
        assertEquals(transaction.getCurrencyShortname(), response.currencyShortName());
        assertEquals(transaction.getSum(), response.sum());
        assertEquals(transaction.getSumInUSD(), response.sumInUSD());
        assertEquals(transaction.getExpenseCategory(), response.expenseCategory());
        assertEquals(transaction.getTransactionDateTime(), response.transactionDateTime());
        assertFalse(response.limitExceeded());

        // verify
        verify(limitService).getCurrentLimit(transaction.getExpenseCategory());
        verify(transactionRepository).save(any(Transaction.class));
    }
}
