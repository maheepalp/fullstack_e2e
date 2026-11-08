package com.example.expensetracker;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:expenses-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class ExpensePersistenceTest {
    @Autowired
    private ExpenseService service;

    @Autowired
    private ExpenseRepository repository;

    @Test
    void persistsAndSearchesExpenseUsingIsolatedTestDatabase() {
        var created = service.create(new ExpenseRequest(
                LocalDate.of(2025, 3, 1), "Groceries", "Fresh market", new BigDecimal("18.75")));

        assertEquals(created, service.findById(created.id()));
        assertEquals(1, service.findAll("FRESH").size());
        assertEquals(1, service.findAll("gro").size());

        service.update(created.id(), new ExpenseRequest(
                LocalDate.of(2025, 3, 2), "Dining", "Lunch", new BigDecimal("22.00")));
        assertEquals("Lunch", service.findById(created.id()).description());

        service.delete(created.id());
        assertFalse(repository.existsById(created.id()));
    }
}
