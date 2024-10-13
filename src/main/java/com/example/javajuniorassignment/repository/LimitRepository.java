package com.example.javajuniorassignment.repository;

import com.example.javajuniorassignment.entity.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    /**
     * Возвращает последний добавленный лимит для указанной категории расходов.
     * <p>
     * Запрос находит последний объект {@link Limit} по полю {@code expenseCategory},
     * сортируя по убыванию идентификаторов (id). Возвращается только один объект.
     * </p>
     *
     * @param expenseCategory категория расходов (например, "Food", "Transport").
     * @return {@link Optional}, содержащий последний лимит, если он найден, иначе пустое значение.
     */
    @Query("SELECT l FROM Limit l WHERE l.expenseCategory = :expenseCategory ORDER BY l.id DESC LIMIT 1")
    Optional<Limit> findLatestPushed(String expenseCategory);
}
