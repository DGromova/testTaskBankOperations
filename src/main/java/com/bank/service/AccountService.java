package com.bank.service;

import com.bank.dto.Transaction;
import com.bank.exception.NotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final UserRepository userRepository;
/*
    @Transactional
    public boolean transfer(Transaction transaction) {
        //проверить баланс
        Integer fromUserId = userRepository.findByLogin(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).get().getId();

        BigDecimal fromAccountBalance = accountRepository.findBalanceByUserId(fromUserId).get();

        if (fromAccountBalance.compareTo(transaction.getAmount()) < 0) {
            BigDecimal toAccountBalance = accountRepository.findBalanceByUserId(transaction.getToUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            toAccountBalance = toAccountBalance.add(transaction.getAmount());
            //списать деньги
            accountRepository.updateBalanceByUserId(transaction.getToUserId(), toAccountBalance);

            return true;
        }
        return true;



    }*/
}
