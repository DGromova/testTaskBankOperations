package com.bank.controller;

import com.bank.dto.Transaction;
import com.bank.exception.ArgumentValidationException;
import com.bank.service.AccountService;
import com.bank.util.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping(("/transfer"))
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transfer(@Valid @RequestBody Transaction transaction, BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Transaction parameters validation exception: {}", exception.getMessage());
        }

        if (accountService.transfer(transaction)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Money transfer completed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
