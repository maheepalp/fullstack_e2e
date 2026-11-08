package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseService {
    private static final Sort EXPENSE_ORDER = Sort.by(
            Sort.Order.desc("expenseDate"),
            Sort.Order.desc("id")
    );

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findAll(String search) {
        List<Expense> expenses = search == null || search.isBlank()
                ? repository.findAll(EXPENSE_ORDER)
                : repository.findByDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByExpenseDateDescIdDesc(
                        search.trim(), search.trim());
        return expenses.stream().map(ExpenseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return ExpenseResponse.from(getExpense(id));
    }

    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = new Expense(
                request.expenseDate(),
                request.category(),
                request.description().trim(),
                request.amount()
        );
        return ExpenseResponse.from(repository.save(expense));
    }

    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = getExpense(id);
        expense.setExpenseDate(request.expenseDate());
        expense.setCategory(request.category());
        expense.setDescription(request.description().trim());
        expense.setAmount(request.amount());
        return ExpenseResponse.from(repository.save(expense));
    }

    public void delete(Long id) {
        repository.delete(getExpense(id));
    }

    private Expense getExpense(Long id) {
        return repository.findById(id).orElseThrow(() -> new ExpenseNotFoundException(id));
    }
}
