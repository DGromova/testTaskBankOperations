package com.bank.util;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PhoneConverter {
    private PhoneConverter() {}

    public static Set<String> convertPhoneFormat(Set<String> phones) {
        return phones.stream()
                .map(PhoneConverter::convertPhoneFormat)
                .collect(Collectors.toSet());
    }

    public static String convertPhoneFormat(String phone) {
        Pattern digitPattern = Pattern.compile("\\D+");
        if (phone.contains("+7")) {
            phone = phone.replace("+7", "8");
        }
        if (!phone.matches("\\D+")) {
            phone = digitPattern.matcher(phone).replaceAll("");
        }
        return phone;
    }
}
