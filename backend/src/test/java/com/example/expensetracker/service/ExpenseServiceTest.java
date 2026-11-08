package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository repository;

    private ExpenseService service;

    @BeforeEach
    void setUp() {
        service = new ExpenseService(repository);
    }

    @Test
    void createsExpense() {
        ExpenseRequest request = request("Coffee");
        when(repository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(request);

        assertEquals("Coffee", result.description());
        assertEquals(new BigDecimal("4.25"), result.amount());
    }

    @Test
    void updatesExistingExpense() {
        Expense existing = new Expense(LocalDate.now(), "Dining", "Lunch", new BigDecimal("10.00"));
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        var result = service.update(7L, request("Dinner"));

        assertEquals("Dinner", result.description());
        verify(repository).save(existing);
    }

    @Test
    void throwsWhenUpdatingMissingExpense() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> service.update(99L, request("Missing")));
    }

    private ExpenseRequest request(String description) {
        return new ExpenseRequest(LocalDate.now(), "Dining", description, new BigDecimal("4.25"));
    }
}
