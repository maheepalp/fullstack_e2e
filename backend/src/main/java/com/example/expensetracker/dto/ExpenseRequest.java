package com.example.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull(message = "Date is required")
        LocalDate expenseDate,

        @NotBlank(message = "Category is required")
        @Pattern(
                regexp = "Groceries|Utilities|Transportation|Dining|Shopping|Healthcare|Other",
                message = "Category must be one of the supported options"
        )
        String category,

        @NotBlank(message = "Description is required")
        @Size(max = 200, message = "Description must be at most 200 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 whole digits and 2 decimal places")
        BigDecimal amount
) {
}
