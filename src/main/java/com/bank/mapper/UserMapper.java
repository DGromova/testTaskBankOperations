package com.bank.mapper;

import com.bank.config.WebSecurityConfig;
import com.bank.dto.UserDtoIn;
import com.bank.exception.PhoneAlreadyExistsException;
import com.bank.models.Account;
import com.bank.models.User;
import com.bank.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserMapper {
    private final WebSecurityConfig webSecurityConfig;
    private final UserRepository userRepository;

    public UserMapper(WebSecurityConfig webSecurityConfig, UserRepository userRepository) {
        this.webSecurityConfig = webSecurityConfig;
        this.userRepository = userRepository;
    }

    public User toEntity(UserDtoIn userDtoIn){
        User user = new User();
        user.setLogin(userDtoIn.getLogin());
        user.setPassword(webSecurityConfig.passwordEncoder().encode(userDtoIn.getPassword()));

        LocalDate birthdate = convertDateFormat(userDtoIn.getBirthdate());
        validateBirthdate(birthdate);
        user.setBirthdate(birthdate);

        user.setSurname(userDtoIn.getSurname());
        user.setName(userDtoIn.getName());
        user.setMiddleName(userDtoIn.getMiddleName());

        Set<String> phones = new HashSet<>(convertPhonesFormat(userDtoIn.getPhones()));
        checkPhones(phones);
        user.setPhones(phones);

        Set<String> emails = userDtoIn.getEmails().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        checkEmails(emails);
        user.setEmails(emails);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(userDtoIn.getBalance());
        user.setAccount(account);

        return user;
    }

    private LocalDate convertDateFormat(String inputBirthdate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate birthdate = LocalDate.parse(inputBirthdate, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String outputBirthdate = outputFormatter.format(birthdate);

        return LocalDate.parse(outputBirthdate, outputFormatter);
    }

    private void validateBirthdate(LocalDate birthdate) {
        if (birthdate.isAfter(LocalDate.now())) {
            throw new DateTimeException("The date of birth cannot be later than the current date");
        }
    }

    private Set<String> convertPhonesFormat(Set<String> phones) {
        Pattern digitPattern = Pattern.compile("\\D+");
        return phones.stream()
                .map(p -> {
                    if (p.contains("+7")) {
                        p = p.replace("+7", "8");
                    }
                    if (!p.matches("\\D+")) {
                        p = digitPattern.matcher(p).replaceAll("");
                    }
                    return p;
                }).collect(Collectors.toSet());
    }

    private void checkPhones(Set<String> phones) {
        Set<String> alreadyExistPhones = new HashSet<>();
        for (String phone : phones) {
            if(userRepository.existsByPhones(phone)) {
                alreadyExistPhones.add(phone);
            }
        }
        if (alreadyExistPhones.size() == 1) {
            throw new PhoneAlreadyExistsException("Phone already exists");
        }
        if (!alreadyExistPhones.isEmpty()) {
            throw new PhoneAlreadyExistsException("Phones " + String.join(", ", alreadyExistPhones) + " already exist");
        }
    }

    private void checkEmails(Set<String> emails) {
        Set<String> alreadyExistEmails = new HashSet<>();
        for (String email : emails) {
            if(userRepository.existsByEmails(email)) {
                alreadyExistEmails.add(email);
            }
        }
        if (alreadyExistEmails.size() == 1) {
            throw new PhoneAlreadyExistsException("Email already exists");
        }
        if (!alreadyExistEmails.isEmpty()) {
            throw new PhoneAlreadyExistsException("Email " + String.join(", ", alreadyExistEmails) + " already exist");
        }
    }

}
