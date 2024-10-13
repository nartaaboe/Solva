package com.example.javajuniorassignment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchangerates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    @Column(precision = 10, scale = 6)
    private BigDecimal rate;
    private Long timestamp;
    private LocalDateTime dateTime;
}
