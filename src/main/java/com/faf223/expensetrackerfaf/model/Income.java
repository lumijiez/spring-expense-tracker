package com.faf223.expensetrackerfaf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "incomes")
public class Income implements IMoneyTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private IncomeCategory category;

    private LocalDate date;
    private BigDecimal amount;
}
