package com.bank.messages;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ErrorMessage {

    private String message;

}
