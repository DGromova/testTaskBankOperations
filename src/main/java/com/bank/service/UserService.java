package com.bank.service;

import com.bank.dto.UserDtoIn;
import com.bank.models.User;
import com.bank.mapper.UserMapper;
import com.bank.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public User createUser(UserDtoIn userDtoIn) {
  //      log.info("User created");
        return userRepository.save(userMapper.toEntity(userDtoIn));
    }



}
