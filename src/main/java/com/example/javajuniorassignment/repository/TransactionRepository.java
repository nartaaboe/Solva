package com.example.javajuniorassignment.repository;

import com.example.javajuniorassignment.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAll(Pageable pageable);
    /**
     * Находит все транзакции, для которых было превышено установленное ограничение.
     *
     * @param isExceeded флаг, указывающий, превышен ли лимит (true/false).
     * @return список транзакций с указанным состоянием превышения лимита.
     */
    List<Transaction> findByLimitExceeded(boolean isExceeded);
    /**
     * Находит все транзакции, совершённые в заданном диапазоне дат.
     * <p>
     * Используется JPQL-запрос для фильтрации транзакций по дате и времени.
     * </p>
     *
     * @param start начало диапазона дат.
     * @param end конец диапазона дат.
     * @return список транзакций, соответствующих диапазону.
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDateTime BETWEEN :start AND :end")
    List<Transaction> findByRange(LocalDateTime start, LocalDateTime end);
    /**
     * Находит все транзакции с указанным состоянием превышения лимита и определённой категорией расходов.
     *
     * @param isExceeded флаг, указывающий, превышен ли лимит (true/false).
     * @param expenseCategory категория расходов, к которой относится транзакция.
     * @return список транзакций, соответствующих условиям.
     */
    List<Transaction> findByLimitExceededAndExpenseCategory(boolean isExceeded, String expenseCategory);
}
