package com.bank.controller;

import com.bank.dto.Transaction;
import com.bank.exception.ArgumentValidationException;
import com.bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /*@PostMapping(("/transfer"))
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transfer(@Valid @RequestBody Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Set<String> messages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toSet());
            throw new ArgumentValidationException(messages.toString());
        }
        if (accountService.transfer(transaction)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Money transfer completed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }*/
}
