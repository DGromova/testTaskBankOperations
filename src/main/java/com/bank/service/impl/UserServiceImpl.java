package com.bank.service.impl;

import com.bank.dto.UserDtoIn;
import com.bank.exception.*;
import com.bank.mapper.UserMapper;
import com.bank.models.EmailDocument;
import com.bank.models.PhoneDocument;
import com.bank.models.User;
import com.bank.models.UserDocument;
import com.bank.repository.UserRepository;
import com.bank.service.UserService;
import com.bank.util.PhoneConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    @Override
    @Transactional
    public boolean createUser(UserDtoIn userDtoIn) {
        if (userRepository.existsByLogin(userDtoIn.getLogin())) {
            log.info("An attempt to create a user with an existing login {}", userDtoIn.getLogin());
            throw new LoginAlreadyExistsException("Login already exists");
        }
        User user = userMapper.toEntity(userDtoIn);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} created", user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean addPhone(String phone) {
        phone = PhoneConverter.convertPhoneFormat(phone);

        //Locking the string User in the database
        User user = userRepository.findByLogin(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user was not found in database");
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                });

        if (elasticsearchOperations.exists(phone, PhoneDocument.class)) {
            log.info("An attempt to add an existing phone number by a user with id = {}", user.getId());
            throw new PhoneAlreadyExistsException("The phone number already exists");
        }

        Set<String> phones= user.getPhones();
        phones.add(phone);
        user.setPhones(phones);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} added a phone number", user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean addEmail(String email) {
        email = email.toLowerCase();

        //Locking the string User in the database
        User user = userRepository.findByLogin(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user was not found in database");
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                });

        if (elasticsearchOperations.exists(email, EmailDocument.class)) {
            log.info("An attempt to add an existing phone number by a user with id = {}", user.getId());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Set<String> emails= user.getEmails();
        emails.add(email);
        user.setEmails(emails);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} added a email", user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean changePhone(String oldPhone, String newPhone) {
        oldPhone = PhoneConverter.convertPhoneFormat(oldPhone);
        newPhone = PhoneConverter.convertPhoneFormat(newPhone);

        //Locking the string User in the database
        User user = userRepository.findByLogin(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user was not found in database");
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                });

        Set<String> userPhones = user.getPhones();

        if (!userPhones.contains(oldPhone)) {
            log.info("An attempt to change a phone number that does not belong to the user with id = {}", user.getId());
            throw new NotFoundException("The replacement phone number not found");
        }
        if (userPhones.contains(newPhone)) {
            log.info("An attempt to add an existing phone number by a user with id = {}", user.getId());
            throw new PhoneAlreadyExistsException("The phone number has already been added earlier");
        } else if (elasticsearchOperations.exists(newPhone, PhoneDocument.class)) {
            log.info("An attempt to add an phone number belonging to another user by a user with id = {}", user.getId());
            throw new PhoneAlreadyExistsException("The phone number is occupied by another user");
        }

        userPhones.remove(oldPhone);
        userPhones.add(newPhone);
        user.setPhones(userPhones);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} changed a phone number", user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean changeEmail(String oldEmail, String newEmail) {
        oldEmail = oldEmail.toLowerCase();
        newEmail = newEmail.toLowerCase();

        //Locking the string User in the database
        User user = userRepository.findByLogin(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user was not found in database");
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                });

        Set <String> userEmails = user.getEmails();

        if (!userEmails.contains(oldEmail)) {
            log.info("An attempt to change a email that does not belong to the user with id = {}", user.getId());
            throw new NotFoundException("The email not found");
        }
        if (userEmails.contains(newEmail)) {
            log.info("An attempt to add an existing email by a user with id = {}", user.getId());
            throw new EmailAlreadyExistsException("The email has already been added");
        } else if (elasticsearchOperations.exists(newEmail, EmailDocument.class)) {
            log.info("An attempt to add an email belonging to another user by a user with id = {}", user.getId());
            throw new EmailAlreadyExistsException("The email is occupied by another user");
        }

        userEmails.remove(oldEmail);
        userEmails.add(newEmail);
        user.setEmails(userEmails);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} changed a email", user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean deletePhone(String phone) {
        String convertedPhone = PhoneConverter.convertPhoneFormat(phone);

        //Locking the string User in the database
        User user = userRepository.findByLogin(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user was not found in database");
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                });

        Set<String> userPhones = user.getPhones();

        if (!userPhones.contains(convertedPhone)) {
            log.info("An attempt to delete a phone number that does not belong to the user by a user with id = {}", user.getId());
            throw new NotFoundException("The phone number not found");
        }
        if (userPhones.size() < 2) {
            log.info("Attempt to delete the last phone number by a user with id = {}", user.getId());
            throw new DeletingLastPhoneException("The last phone number cannot be deleted");
        }

        userPhones.removeIf(p -> p.equals(convertedPhone));
        user.setPhones(userPhones);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} deleted a phone number", user.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean deleteEmail(String email) {
        email = email.toLowerCase();

        //Locking the string User in the database
        User user = userRepository.findByLogin(getCurrentUserLogin())
                .orElseThrow(() -> {
                    log.error("Authenticated user was not found in database");
                    return new AuthenticatedUserNotFoundException("Authenticated user was not found");
                });

        Set<String> userEmails = new HashSet<>(user.getEmails());

        if (!userEmails.contains(email)) {
            log.info("An attempt to delete a email that does not belong to the user by a user with id = {}", user.getId());
            throw new NotFoundException("The email not found");
        }
        if (userEmails.size() <2) {
            log.info("Attempt to delete the last email by a user with id = {}", user.getId());
            throw new DeletingLastEmailException("The last email cannot be deleted");
        }

        String finalEmail = email;
        userEmails.removeIf(e -> e.equals(finalEmail));
        user.setEmails(userEmails);

        //Saving in DB and Elasticsearch
        saveUserToBoth(user);
        log.info("User with id = {} deleted a email", user.getId());
        return true;
    }

    private String getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) {
            log.error("An attempt to access a method that requires authentication without authentication");
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }
        return authentication.getName();
    }

    private void saveUserToBoth(User user) {
        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));
    }

}

