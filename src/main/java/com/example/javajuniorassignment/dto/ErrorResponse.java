package com.example.javajuniorassignment.dto;

import java.time.LocalDateTime;

public record ErrorResponse (
        LocalDateTime localDateTime,
        String message,
        int status,
        String details
){
}
