package com.example.javajuniorassignment.service.impl;

import com.example.javajuniorassignment.dto.LimitResponse;
import com.example.javajuniorassignment.dto.TransactionRequest;
import com.example.javajuniorassignment.dto.TransactionResponse;
import com.example.javajuniorassignment.entity.Transaction;
import com.example.javajuniorassignment.repository.TransactionRepository;
import com.example.javajuniorassignment.service.ExchangeRateService;
import com.example.javajuniorassignment.service.LimitService;
import com.example.javajuniorassignment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final LimitService limitService;
    private final ExchangeRateService exchangeRateService;
    /**
     * Создает новую транзакцию на основе запроса.
     * Проверяет корректность данных, конвертирует валюту при необходимости,
     * проверяет лимит и сохраняет транзакцию.
     *
     * @param transactionRequest запрос на создание транзакции
     * @return TransactionResponse ответ с данными сохраненной транзакции
     * @throws IllegalArgumentException если данные запроса некорректны
     */
    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        validateTransactionRequest(transactionRequest);

        Transaction transaction = toEntity(transactionRequest);
        transaction.setSumInUSD(getSumInUSD(transaction));

        LimitResponse currentLimit = limitService.getCurrentLimit(transaction.getExpenseCategory());
        if (isLimitValid(currentLimit)) {
            log.info(currentLimit.toString());
            BigDecimal totalSum = calculateTotalSum(currentLimit, transaction);
            transaction.setLimitExceeded(totalSum.compareTo(currentLimit.limitSum()) > 0);
        } else {
            log.info("Лимит не действителен.");
            transaction.setLimitExceeded(false);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    /**
     * Валидирует запрос на создание транзакции.
     * Проверяет, чтобы сумма была положительной,
     * счет отправителя и получателя не совпадали, а категория расходов была указана.
     *
     * @param transactionRequest запрос на создание транзакции
     * @throws IllegalArgumentException если данные запроса некорректны
     */
    private void validateTransactionRequest(TransactionRequest transactionRequest) {
        if (transactionRequest.sum().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть положительной.");
        }
        if (transactionRequest.accountFrom().equals(transactionRequest.accountTo())) {
            throw new IllegalArgumentException("Нельзя совершить транзакцию на тот же счет.");
        }
        if (transactionRequest.expenseCategory() == null) {
            throw new IllegalArgumentException("Нужно указать категорию транзакции.");
        }
    }
    /**
     * Конвертирует сумму транзакции в USD.
     * Если валюта уже USD, возвращает сумму без изменений.
     *
     * @param transaction объект транзакции
     * @return сумма в долларах США
     */
    private BigDecimal getSumInUSD(Transaction transaction) {
        return transaction.getCurrencyShortname().equals("USD") ? transaction.getSum() :
                exchangeRateService.convertToUSD(transaction.getCurrencyShortname(), transaction.getSum());
    }
    /**
     * Проверяет, является ли текущий лимит актуальным.
     *
     * @param currentLimit объект лимита
     * @return true, если лимит актуален, иначе false
     */
    private boolean isLimitValid(LimitResponse currentLimit) {
        return currentLimit != null && currentLimit.limitDateTime().isAfter(LocalDateTime.now().minusMonths(1));
    }
    /**
     * Вычисляет общую сумму транзакций в заданной категории за последний месяц.
     *
     * @param currentLimit текущий лимит
     * @param transaction текущая транзакция
     * @return общая сумма транзакций в USD
     */
    private BigDecimal calculateTotalSum(LimitResponse currentLimit, Transaction transaction) {
        BigDecimal totalSum = transaction.getSumInUSD();
        log.info("Текущая сумма транзакции в USD: " + totalSum);

        List<Transaction> transactions = transactionRepository.findByRange(
                currentLimit.limitDateTime(),
                currentLimit.limitDateTime().plusMonths(1)
        );

        for (Transaction t : transactions) {
            if (t.getExpenseCategory().equals(currentLimit.expenseCategory())) {
                totalSum = totalSum.add(t.getSumInUSD());
            }
        }

        log.info("Общая сумма для категории: " + totalSum);
        return totalSum;
    }
    /**
     * Возвращает список транзакций, превышающих лимит.
     * Если категория не указана, возвращает все транзакции с превышением лимита.
     *
     * @param expenseCategory категория расходов (может быть null)
     * @return список транзакций, превышающих лимит
     */
    @Override
    public List<TransactionResponse> getTransactionExceedingLimit(String expenseCategory) {
        if(expenseCategory == null){
            return transactionRepository.findByLimitExceeded(true)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }
        return transactionRepository.findByLimitExceededAndExpenseCategory(true, expenseCategory)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    /**
     * Возвращает список транзакций с пагинацией.
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return список транзакций на указанной странице
     */
    @Override
    public List<TransactionResponse> getTransactions(int page, int size) {
        return transactionRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    private TransactionResponse mapToResponse(Transaction transaction){
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountFrom(),
                transaction.getAccountTo(),
                transaction.getCurrencyShortname(),
                transaction.getSum(),
                transaction.getSumInUSD(),
                transaction.getExpenseCategory(),
                transaction.getTransactionDateTime(),
                transaction.isLimitExceeded()
        );
    }
    private Transaction toEntity(TransactionRequest transactionRequest){
        return Transaction.builder()
                .accountFrom(transactionRequest.accountFrom())
                .accountTo(transactionRequest.accountTo())
                .currencyShortname(transactionRequest.currencyShortname())
                .sum(transactionRequest.sum())
                .sumInUSD(transactionRequest.currencyShortname().equals("USD") ? transactionRequest.sum() : null)
                .expenseCategory(transactionRequest.expenseCategory())
                .transactionDateTime(LocalDateTime.now())
                .build();
    }
}
