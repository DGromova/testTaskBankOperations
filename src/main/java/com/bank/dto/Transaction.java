package com.bank.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transaction {
    @NotNull(message = "Enter the user's ID")
    Integer toUserId;

    @NotNull(message = "Enter amount")
    @Digits(integer = 10, fraction = 2, message = "Enter the amount with two digits after the decimal point")
    @Positive(message = "Amount cannot be negative or zero")
    BigDecimal amount;

}
