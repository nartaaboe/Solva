package com.example.javajuniorassignment.service;


import com.example.javajuniorassignment.dto.LimitRequest;
import com.example.javajuniorassignment.dto.LimitResponse;

public interface LimitService {
    LimitResponse setNewLimit(LimitRequest limitRequest);
    LimitResponse getCurrentLimit(String expenseCategory);
    LimitResponse removeLimit(String expenseCategory);
}
