package com.faf223.expensetrackerfaf.service;

import com.faf223.expensetrackerfaf.model.Credential;
import com.faf223.expensetrackerfaf.model.IMoneyTransaction;
import com.faf223.expensetrackerfaf.model.Income;
import com.faf223.expensetrackerfaf.model.User;
import com.faf223.expensetrackerfaf.repository.CredentialRepository;
import com.faf223.expensetrackerfaf.repository.IncomeRepository;
import com.faf223.expensetrackerfaf.repository.UserRepository;
import com.faf223.expensetrackerfaf.util.exceptions.UserNotAuthenticatedException;
import com.faf223.expensetrackerfaf.util.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService implements ITransactionService {

    private final IncomeRepository incomeRepository;
    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;

    public void createOrUpdate(IMoneyTransaction income) {
        incomeRepository.save((Income) income);
    }

    public List<Income> getTransactions() {
        return incomeRepository.findAll();
    }

    public List<Income> getTransactionsByEmail(String email) {

        Optional<Credential> credential = credentialRepository.findByEmail(email);
        if (credential.isPresent()) {
            return incomeRepository.findByUser(credential.get().getUser());
        }

        return new ArrayList<>();
    }

    @Override
    public List<Income> getTransactionsByDate(LocalDate date) {
        return incomeRepository.findByDate(date);
    }

    @Override
    public List<Income> getTransactionsByDate(LocalDate date, String email) {
        return getTransactionsByDate(date)
                .stream()
                .filter(transaction -> {
                    Optional<Credential> credential = credentialRepository.findByEmail(email);
                    if(credential.isEmpty())
                        throw new UserNotFoundException("The user has not been found");
                    return credential.get().getUser().equals(transaction.getUser());
                })
                .toList();
    }

    // TODO: store transaction month in a separate field in the DB and change this logic
    @Override
    public List<Income> getTransactionsByMonth(Month month) {
        LocalDate startOfMonth = LocalDate.of(LocalDate.now().getYear(), month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        return incomeRepository.findByDateBetween(startOfMonth, endOfMonth);
    }

    @Override
    public List<Income> getTransactionsByMonth(Month month, String email) {
        return getTransactionsByMonth(month)
                .stream()
                .filter(transaction -> {
                    Optional<Credential> credential = credentialRepository.findByEmail(email);
                    if(credential.isEmpty())
                        throw new UserNotFoundException("The user has not been found");
                    return credential.get().getUser().equals(transaction.getUser());
                })
                .toList();
    }

    public Income getTransactionById(long id) {
        return incomeRepository.findById(id).orElse(null);
    }

    public void deleteTransactionById(long id) {
        incomeRepository.deleteById(id);
    }

    @Override
    public boolean belongsToUser(IMoneyTransaction transaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

            if(authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ADMIN"))) {

                Optional<Credential> credential = credentialRepository.findByEmail(userDetails.getUsername());
                if(credential.isEmpty()) throw new UserNotFoundException("The user has not been found");
                Optional<User> user = userRepository.findById(credential.get().getUser().getUserUuid());
                if(user.isEmpty()) throw new UserNotFoundException("The user has not been found");

                return user.get().getIncomes().contains((Income) transaction);

            }

        }

        throw new UserNotAuthenticatedException("You are not authenticated");
    }
}
