package com.bank.service.impl;

import com.bank.dto.Transaction;
import com.bank.exception.*;
import com.bank.models.Account;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Retryable(
            noRetryFor = {
                    InsufficientFundsInTheAccountException.class,
                    NotFoundException.class,
                    AuthUsersAccountNotFoundException.class,
                    AuthenticatedUserNotFoundException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 50,
                    maxDelay = 4100,
                    multiplier = 3.0,
                    random = true
            )
    )
    @Transactional
    public boolean transfer(Transaction transaction) {
        Long fromUserId = userRepository.findByLoginWithoutLock(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user {} was not found in database", getCurrentUserLogin());
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                })
                .getId();

        //Locking accounts
        Account fromUserAccount = accountRepository.findAccountByUserId(fromUserId)
                .orElseThrow(() -> {
                    log.error("Authenticated user's account was not found in database for the user with the id = {}", fromUserId);
                    return new AuthUsersAccountNotFoundException("Authenticated user's account was not found");
                });
        Account toUserAccount = accountRepository.findAccountByUserId(transaction.getToUserId())
                .orElseThrow(()-> {
                    log.info("The account with user ID = {} requested by the user with ID = {} was not found", transaction.getToUserId(), fromUserId);
                    return new NotFoundException("Account with the entered user's ID was not found");
                });

        if (transaction.getAmount().compareTo(fromUserAccount.getCurrentBalance())<=0) {
            BigDecimal fromUserBalance = fromUserAccount.getCurrentBalance()
                    .subtract(transaction.getAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal toUserBalance = toUserAccount.getCurrentBalance()
                    .add(transaction.getAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            accountRepository.updateCurrentBalanceByUserId(fromUserId, fromUserBalance);
            accountRepository.updateCurrentBalanceByUserId(transaction.getToUserId(), toUserBalance);
        } else {
            throw new InsufficientFundsInTheAccountException("Insufficient funds in the account");
        }

        log.info("Funds were transferred from the user with ID = {} to the user with ID = {}", fromUserId, transaction.getToUserId());
        return true;
    }


    private String getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) {
            log.error("An attempt to access a method that requires authentication without authentication");
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }
        return authentication.getName();
    }

}
