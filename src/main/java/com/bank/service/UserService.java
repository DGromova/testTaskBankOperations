package com.bank.service;

import com.bank.dto.UserDtoIn;
import com.bank.exception.LoginAlreadyExistsException;
import com.bank.exception.PhoneAlreadyExistsException;
import com.bank.mapper.UserMapper;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    //@Transactional
    public boolean createUser(UserDtoIn userDtoIn) {
        if (userRepository.existsByLogin(userDtoIn.getLogin())) {
            throw new LoginAlreadyExistsException("Login already exists");
        }
        userRepository.save(userMapper.toEntity(userDtoIn));
        log.info("User created");
        return true;
    }

  /*  public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }*/

  /*  @Transactional
    public boolean addPhone(String phone) {
        if (userRepository.existsByPhones(phone)) {
            throw new PhoneAlreadyExistsException("The phone number already exists");
        }
        Optional <User> user = userRepository.findByLogin(authProvider.getPrincipal());
        userRepository.save()


//        userRepository.save();
    }*/
}

