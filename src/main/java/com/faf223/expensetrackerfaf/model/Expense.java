package com.faf223.expensetrackerfaf.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;

    private LocalDate date;
    private BigDecimal amount;
}

