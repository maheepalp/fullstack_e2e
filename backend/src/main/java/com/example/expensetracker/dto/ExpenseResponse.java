package com.example.expensetracker.dto;

import com.example.expensetracker.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, LocalDate expenseDate, String category, String description, BigDecimal amount) {
    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getExpenseDate(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getAmount()
        );
    }
}
