package com.example.javajuniorassignment.controller;

import com.example.javajuniorassignment.dto.LimitRequest;
import com.example.javajuniorassignment.dto.LimitResponse;
import com.example.javajuniorassignment.service.LimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/limit")
@RequiredArgsConstructor
@Tag(name = "Limit API", description = "API для управления лимитами расходов")
public class LimitController {
    private final LimitService limitService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Установить новый лимит",
            description = "Создает новый лимит расходов")
    public LimitResponse setNewLimit(@RequestBody LimitRequest limitRequest){
        return limitService.setNewLimit(limitRequest);
    }
    @DeleteMapping
    @Operation(summary = "Удалить лимит",
            description = "Удаляет текущий лимит расходов")
    public LimitResponse removeLimit(@RequestParam(defaultValue = "products") String expenseCategory){
        return limitService.removeLimit(expenseCategory);
    }
    @GetMapping
    @Operation(summary = "Получить текущий лимит",
            description = "Возвращает текущий лимит расходов")
    public LimitResponse getCurrentLimit(@RequestParam(defaultValue = "products") String expenseCategory){
        return limitService.getCurrentLimit(expenseCategory);
    }
}
