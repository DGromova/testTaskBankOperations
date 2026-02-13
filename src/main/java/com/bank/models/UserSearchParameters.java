package com.bank.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserSearchParameters {
    private String birthdate;
    private String phone;
    private String fullName;
    private String email;
}
