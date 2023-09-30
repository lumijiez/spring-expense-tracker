package com.faf223.expensetrackerfaf.controller;

import com.faf223.expensetrackerfaf.model.Income;
import com.faf223.expensetrackerfaf.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/incomes")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<List<Income>> getIncomesByUser(@PathVariable String userUuid) {
        List<Income> incomes = incomeService.getIncomesByUserId(userUuid);
        if (!incomes.isEmpty()) {
            return ResponseEntity.ok(incomes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
