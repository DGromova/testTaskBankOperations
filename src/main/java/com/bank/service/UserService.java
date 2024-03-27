package com.bank.service;

import com.bank.entity.User;
import com.bank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
//    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
     //   LOG.info("User created");
        return null;
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

}
