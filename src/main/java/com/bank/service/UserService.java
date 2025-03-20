package com.bank.service;

import com.bank.dto.UserDtoIn;
import com.bank.exception.*;
import com.bank.mapper.UserMapper;
import com.bank.models.User;
import com.bank.models.UserDocument;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public boolean createUser(UserDtoIn userDtoIn) {
        if (userRepository.existsByLogin(userDtoIn.getLogin())) {
            throw new LoginAlreadyExistsException("Login already exists");
        }
        User user = userMapper.toEntity(userDtoIn);
        userRepository.save(user);

        UserDocument userDocument = new UserDocument(user);
        elasticsearchOperations.save(userDocument);

        log.info("User created");
        return true;
    }

    @Transactional
    public boolean addPhone(String phone) {
        validPhone(phone);
        phone = userMapper.convertPhoneFormat(phone);

        if (userRepository.existsByPhones(phone)) {
            throw new PhoneAlreadyExistsException("The phone number already exists");
        }

        Integer authUserId = getAuthUserId();
        User user = userRepository.findById(authUserId).get();

        Set<String> phones= user.getPhones();
        phones.add(phone);
        user.setPhones(phones);

        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));

        return true;
    }

    @Transactional
    public boolean addEmail(String email) {
        validEmail(email);
        email = email.toLowerCase();

        if (userRepository.existsByEmails(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Integer authUserId = getAuthUserId();
        User user = userRepository.findById(authUserId).get();

        Set<String> emails= user.getEmails();
        emails.add(email);
        user.setEmails(emails);

        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));

        return true;
    }

    @Transactional
    public boolean changePhone(String oldPhone, String newPhone) {
        validPhone(oldPhone);
        validPhone(newPhone);
        oldPhone = userMapper.convertPhoneFormat(oldPhone);
        newPhone = userMapper.convertPhoneFormat(newPhone);

        Integer authUserId = getAuthUserId();
        User user = userRepository.findById(authUserId).get();

        List<String> phonesList = new ArrayList<>(user.getPhones());

        if (userRepository.existsByPhones(newPhone) & !phonesList.contains(newPhone)) {
            throw new PhoneAlreadyExistsException("The phone number is occupied by another user");
        }

        if (phonesList.contains(newPhone)) {
            throw new PhoneAlreadyExistsException("The phone number has already been added");
        }

        if (!phonesList.contains(oldPhone)) {
            throw new NotFoundException("The phone number not found");
        }

        for (int i = 0; i < phonesList.size(); i++) {
            if (phonesList.get(i).equals(oldPhone)) {
                phonesList.set(i, newPhone);
            }
        }

        user.setPhones(Set.of(String.valueOf(phonesList)));

        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));

        return true;
    }

    @Transactional
    public boolean changeEmail(String oldEmail, String newEmail) {
        validEmail(oldEmail);
        validEmail(newEmail);
        oldEmail = oldEmail.toLowerCase();
        newEmail = newEmail.toLowerCase();

        Integer authUserId = getAuthUserId();
        User user = userRepository.findById(authUserId).get();

        List<String> emailsList = new ArrayList<>(user.getEmails());

        if (userRepository.existsByEmails(newEmail) & !emailsList.contains(newEmail)) {
            throw new EmailAlreadyExistsException("The email is occupied by another user");
        }

        if (emailsList.contains(newEmail)) {
            throw new EmailAlreadyExistsException("The email has already been added");
        }

        if (!emailsList.contains(oldEmail)) {
            throw new NotFoundException("The email not found");
        }

        for (int i = 0; i < emailsList.size(); i++) {
            if (emailsList.get(i).equals(oldEmail)) {
                emailsList.set(i, newEmail);
            }
        }

        user.setEmails(Set.of(String.valueOf(emailsList)));

        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));

        return true;
    }

    @Transactional
    public boolean deletePhone(String phone) {
        validPhone(phone);
        phone = userMapper.convertPhoneFormat(phone);

        Integer authUserId = getAuthUserId();
        User user = userRepository.findById(authUserId).get();

        Set<String> phonesList = new HashSet<>(user.getPhones());

        if (!phonesList.contains(phone)) {
            throw new NotFoundException("The phone number not found");
        }
        if (phonesList.size() <2) {
            throw new DeletingLastPhoneException("The last phone number cannot be deleted");
        }

        String finalPhone = phone;
        phonesList.removeIf(p -> p.equals(finalPhone));
        user.setPhones(phonesList);

        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));

        return true;
    }

    @Transactional
    public boolean deleteEmail(String email) {
        validEmail(email);
        email = email.toLowerCase();

        Integer authUserId = getAuthUserId();
        User user = userRepository.findById(authUserId).get();

        Set<String> emailsList = new HashSet<>(user.getEmails());

        if (!emailsList.contains(email)) {
            throw new NotFoundException("The email not found");
        }
        if (emailsList.size() <2) {
            throw new DeletingLastEmailException("The last email cannot be deleted");
        }

        String finalEmail = email;
        emailsList.removeIf(p -> p.equals(finalEmail));
        user.setEmails(emailsList);

        userRepository.save(user);
        elasticsearchOperations.save(new UserDocument(user));

        return true;
    }

    private void validPhone(String phone) {
        if (!phone.matches("^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$")) {
            throw new ArgumentValidationException("The phone number must start with +7 or 8. For a landline phone, enter the area code");
        }
    }

    private void validEmail(String email) {
        if(!email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")) {
            throw new ArgumentValidationException("Invalid email format");
        }
    }

    private Integer getAuthUserId() {
        return userRepository.findByLogin(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).get().getId();
    }

}

