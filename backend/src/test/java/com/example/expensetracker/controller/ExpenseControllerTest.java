package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService service;

    @Test
    void returnsExpensesAndForwardsSearch() throws Exception {
        when(service.findAll("gro")).thenReturn(List.of(response()));

        mockMvc.perform(get("/api/expenses").param("search", "gro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Groceries"));
    }

    @Test
    void createsValidExpense() throws Exception {
        when(service.create(any())).thenReturn(response());

        mockMvc.perform(post("/api/expenses")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"expenseDate":"2025-03-01","category":"Groceries",
                                 "description":"Market","amount":12.5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(12.50));
    }

    @Test
    void rejectsInvalidExpense() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"expenseDate":"2025-03-01","category":"Unknown",
                                 "description":"","amount":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.category").exists())
                .andExpect(jsonPath("$.fieldErrors.description").exists())
                .andExpect(jsonPath("$.fieldErrors.amount").exists());
    }

    @Test
    void returnsNotFoundForMissingExpense() throws Exception {
        when(service.findById(44L)).thenThrow(new ExpenseNotFoundException(44L));

        mockMvc.perform(get("/api/expenses/44"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense with ID 44 was not found"));
    }

    @Test
    void deletesExpense() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());
    }

    private ExpenseResponse response() {
        return new ExpenseResponse(1L, LocalDate.of(2025, 3, 1), "Groceries", "Market", new BigDecimal("12.50"));
    }
}
