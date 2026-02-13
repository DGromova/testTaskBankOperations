package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "Enter login")
    private String login;
    @NotBlank(message = "Enter password")
    private String password;
}
