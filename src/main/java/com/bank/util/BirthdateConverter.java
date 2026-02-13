package com.bank.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BirthdateConverter {

    private BirthdateConverter() {}

    public static LocalDate convertDateFormat(String inputBirthdate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate birthdate = LocalDate.parse(inputBirthdate, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String outputBirthdate = outputFormatter.format(birthdate);

        return LocalDate.parse(outputBirthdate, outputFormatter);
    }

}
