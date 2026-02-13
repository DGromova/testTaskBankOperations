package com.bank.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Transaction {
    @NotNull(message = "Enter the user's ID")
    Long toUserId;

    @NotNull(message = "Enter amount")
    @Digits(integer = 10, fraction = 2, message = "Enter the amount with two digits after the decimal point")
    @Positive(message = "Amount cannot be negative or zero")
    BigDecimal amount;

}
