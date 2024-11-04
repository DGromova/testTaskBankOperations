package com.bank.mapper;

import com.bank.config.WebSecurityConfig;
import com.bank.dto.UserDtoIn;
import com.bank.exception.EmailAlreadyExistsException;
import com.bank.exception.LoginAlreadyExistsException;
import com.bank.exception.PhoneAlreadyExistsException;
import com.bank.models.Account;
import com.bank.models.User;
import com.bank.repository.UserRepository;
import com.bank.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
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

        String login = userDtoIn.getLogin();
        if (userRepository.existsByLogin(login)) {
            throw new LoginAlreadyExistsException("Login " + login + " already exists");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(webSecurityConfig.passwordEncoder().encode(userDtoIn.getPassword()));

        LocalDate birthdate = convertDateFormat(userDtoIn.getBirthdate());
        validateBirthdate(birthdate);
        user.setBirthdate(birthdate);

        user.setSurname(userDtoIn.getSurname());
        user.setName(userDtoIn.getName());
        user.setMiddleName(userDtoIn.getMiddleName());


        Set<String> phones = convertPhonesFormat(userDtoIn.getPhones());
  //      if (userRepository.findByPhone(phones.stream().toString())) {
  //          throw new PhoneAlreadyExistsException("Phone already exists");
   //     }
      //  checkPhones(phones);
        user.setPhones(phones);

        Set<String> emails = new HashSet<>(userDtoIn.getEmails());
        emails.forEach(String::toLowerCase);
      //  checkEmails(emails);
        user.setEmails(emails);



  //      user.setEmails(new HashSet<>(userDtoIn.getEmails()));
        Account account = new Account();
        account.setUser(user);
        account.setBalance(userDtoIn.getBalance());
        user.setAccount(account);

        return user;
    }

    /*private void checkPhones(Set<String> phones) throws PhoneAlreadyExistsException {
        if (userRepository.findByPhone(phones.stream().toString())) {
            throw new PhoneAlreadyExistsException("Phone already exists");
        }
    }*/

  /*  private void checkEmails(Set<String> emails) throws EmailAlreadyExistsException {
        List<String> emailsList = new ArrayList<>(emails.stream().toList());
        List<String> allEmails = new ArrayList<>(userRepository.findAllEmails());
        emailsList.retainAll(allEmails);
        if (emailsList.size() == 1) {
            throw new EmailAlreadyExistsException("Email " + emails.toArray()[0] + " already exists");
        }
        if (!emailsList.isEmpty()) {
            throw new EmailAlreadyExistsException("Emails " + String.join(", ", emails) + " already exist");
        }
    }*/

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
                        p.replace("+7", "8");
                    }
                    if (!p.matches("\\D+")) {
                        p = digitPattern.matcher(p).replaceAll("");
                    }
                    return p;
                }).collect(Collectors.toSet());
    }

}
