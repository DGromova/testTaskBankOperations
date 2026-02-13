package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.Set;

@Getter
public class UserDtoIn {

    @NotBlank(message = "Enter login")
    @Size(min = 3, max = 20, message = "The login must contain from 3 to 20 characters")
    private String login;

    @NotBlank(message = "Enter password")
    @Pattern(regexp = "^[\\S]*$", message = "The password must not contain spaces")
    @Size(min = 8, max = 20, message = "The password must contain from 8 to 20 characters")
    private String password;

    @NotBlank(message = "Enter birthdate")
    @Pattern(regexp = "(\\d{2})\\.(\\d{2})\\.(19|20)\\d{2}", message = "Enter the birthdate in the format dd.MM.yyyy")
    private String birthdate;

    @NotBlank(message = "Enter surname")
    @Size(max = 50, message = "The length of the surname should not exceed 50 characters")
    private String surname;

    @NotBlank(message = "Enter name")
    @Size(max = 50, message = "The length of the name should not exceed 50 characters")
    private String name;

    @Size(max = 50, message = "The length of the middle name should not exceed 50 characters")
    private String middleName;

    @NotEmpty(message = "Enter one or more phone numbers")
    private Set<@Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
            message = "The phone number must start with +7 or 8. For a landline phone, enter the area code") String> phones;

    @NotEmpty(message = "Enter one or more emails")
    private Set<@Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid email format") String> emails;

    @NotNull(message = "Enter initial balance")
    @Digits(integer = 10, fraction = 2, message = "Enter the initial balance with two digits after the decimal point")
    @PositiveOrZero(message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;
}
