package com.bank.service;

import com.bank.dto.Transaction;
import com.bank.exception.InsufficientFundsInTheAccountException;
import com.bank.exception.NotFoundException;
import com.bank.models.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean transfer(Transaction transaction) {
        Integer fromUserId = userRepository.findByLogin(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).get().getId();

        Account fromUserAccount = accountRepository.findAccountByUserId(fromUserId).get();

        Account toUserAccount = accountRepository.findAccountByUserId(transaction.getToUserId())
                .orElseThrow(()->new NotFoundException("The user with the entered ID was not found"));

        if (transaction.getAmount().compareTo(fromUserAccount.getBalance())<=0) {
            BigDecimal fromUserBalance = fromUserAccount.getBalance().subtract(transaction.getAmount());
            BigDecimal toUserBalance = toUserAccount.getBalance().add(transaction.getAmount());

            accountRepository.updateBalanceByUserId(fromUserId, fromUserBalance);
            accountRepository.updateBalanceByUserId(transaction.getToUserId(), toUserBalance);
        } else {
            throw new InsufficientFundsInTheAccountException("Insufficient funds in the account");
        }

        return true;
    }
}
