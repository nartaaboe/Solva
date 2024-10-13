package com.example.javajuniorassignment.repository;

import com.example.javajuniorassignment.entity.ExchangeRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Page<ExchangeRate> findAll(Pageable pageable);
    /**
     * Возвращает последний добавленный курс обмена для указанного символа.
     * <p>
     * Запрос находит последний объект {@link ExchangeRate} по полю {@code symbol},
     * сортируя по убыванию идентификаторов (id). Возвращается только один объект.
     * </p>
     *
     * @param symbol символ валютной пары (например, "USD/KZT").
     * @return {@link Optional}, содержащий последний курс обмена, если он найден, иначе пустое значение.
     */
    @Query("SELECT l FROM ExchangeRate l WHERE l.symbol = :symbol ORDER BY l.id DESC LIMIT 1")
    Optional<ExchangeRate> findLatestPushed(String symbol);
}
