package com.bank.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilterParams {

    private String birthdate;
    private String phone;
    private String fullName;
    private String email;

}
