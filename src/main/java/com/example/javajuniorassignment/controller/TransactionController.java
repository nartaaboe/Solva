package com.example.javajuniorassignment.controller;

import com.example.javajuniorassignment.dto.TransactionRequest;
import com.example.javajuniorassignment.dto.TransactionResponse;
import com.example.javajuniorassignment.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
@Tag(name = "Transaction API", description = "API для управления транзакциями")
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping
    @Operation(summary = "Создать новую транзакцию",
            description = "Создает новую транзакцию, которая проверяет лимит транзакции и конвертируют валюты в USD")
    public TransactionResponse createTransaction(@RequestBody TransactionRequest transactionRequest){
        return transactionService.createTransaction(transactionRequest);
    }
    @GetMapping("/exceeding-limit")
    @Operation(summary = "Получить транзакции, превышающие лимит",
            description = "Возвращает все транзакции, превышающие установленный лимит, с возможностью фильтрации по категории расходов")
    public List<TransactionResponse> getTransactionsExceedingLimit(
            @RequestParam(required = false) String expenseCategory
    ){
        return transactionService.getTransactionExceedingLimit(expenseCategory);
    }
    @GetMapping
    @Operation(summary = "Получить все транзакции",
            description = "Возвращает список транзакций с поддержкой постраничной навигации, с размером страницы по умолчанию 10")
    public List<TransactionResponse> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return transactionService.getTransactions(page, size);
    }
}
