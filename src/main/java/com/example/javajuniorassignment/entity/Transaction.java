package com.example.javajuniorassignment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountFrom;
    private String accountTo;
    private String currencyShortname;
    private BigDecimal sum;
    @Column(name = "sum_in_usd", nullable = false)
    private BigDecimal sumInUSD;
    private String expenseCategory;
    private LocalDateTime transactionDateTime;
    private boolean limitExceeded;

}
