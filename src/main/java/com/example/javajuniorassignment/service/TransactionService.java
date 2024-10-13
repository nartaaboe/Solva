package com.example.javajuniorassignment.service;


import com.example.javajuniorassignment.dto.TransactionRequest;
import com.example.javajuniorassignment.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    List<TransactionResponse> getTransactionExceedingLimit(String expenseCategory);
    List<TransactionResponse> getTransactions(int page, int size);
}
