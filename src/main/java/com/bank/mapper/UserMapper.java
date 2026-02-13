package com.bank.mapper;

import com.bank.config.WebSecurityConfig;
import com.bank.dto.UserDtoIn;
import com.bank.exception.DateTimeValidationException;
import com.bank.exception.PhoneAlreadyExistsException;
import com.bank.models.Account;
import com.bank.models.User;
import com.bank.repository.UserRepository;
import com.bank.util.BirthdateConverter;
import com.bank.util.PhoneConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
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

        LocalDate birthdate = BirthdateConverter.convertDateFormat(userDtoIn.getBirthdate());
        validateBirthdate(birthdate);
        user.setBirthdate(birthdate);

        user.setSurname(userDtoIn.getSurname().toUpperCase());
        user.setName(userDtoIn.getName().toUpperCase());
        user.setMiddleName(userDtoIn.getMiddleName().toUpperCase());

        Set<String> phones = new HashSet<>(PhoneConverter.convertPhoneFormat(userDtoIn.getPhones()));
        checkPhones(phones);
        user.setPhones(phones);

        Set<String> emails = userDtoIn.getEmails().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        checkEmails(emails);
        user.setEmails(emails);

        Account account = new Account();
        account.setUser(user);
        account.setInitialBalance(userDtoIn.getInitialBalance());
        account.setCurrentBalance(userDtoIn.getInitialBalance());
        user.setAccount(account);

        return user;
    }

    private void validateBirthdate(LocalDate birthdate) {
        if (birthdate.isAfter(LocalDate.now())) {
            log.info("An attempt to set a date of birth later than the current one");
            throw new DateTimeValidationException("The date of birth cannot be later than the current date");
        }
    }

    private void checkPhones(Set<String> phones) {
        Set<String> alreadyExistPhones = new HashSet<>();
        for (String phone : phones) {
            if(userRepository.existsByPhonesContains(phone)) {
                alreadyExistPhones.add(phone);
            }
        }
        if (alreadyExistPhones.size() == 1) {
            log.info("Attempt to create an existing phone number");
            throw new PhoneAlreadyExistsException("Phone already exists");
        }
        if (!alreadyExistPhones.isEmpty()) {
            log.info("Attempt to create existing phone numbers");
            throw new PhoneAlreadyExistsException("Phones " + String.join(", ", alreadyExistPhones) + " already exist");
        }
    }

    private void checkEmails(Set<String> emails) {
        Set<String> alreadyExistEmails = new HashSet<>();
        for (String email : emails) {
            if(userRepository.existsByEmailsContains(email)) {
                alreadyExistEmails.add(email);
            }
        }
        if (alreadyExistEmails.size() == 1) {
            log.info("Attempt to create an existing email address");
            throw new PhoneAlreadyExistsException("Email address already exists");
        }
        if (!alreadyExistEmails.isEmpty()) {
            log.info("Attempt to create existing email addresses");
            throw new PhoneAlreadyExistsException("Email addresses " + String.join(", ", alreadyExistEmails) + " already exist");
        }
    }

}
