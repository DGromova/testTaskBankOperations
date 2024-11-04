package com.bank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDtoOut {
    private String login;
    private LocalDate birthdate;
    private String surname;
    private String name;
    private String middleName;
    private Set<String> phones;
    private Set<String> emails;
    private BigDecimal balance;
}
