package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Enter login")
    private String login;
    @NotBlank(message = "Enter password")
    private String password;
}
