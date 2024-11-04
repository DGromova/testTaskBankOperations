package com.bank.controller;

import com.bank.dto.UserDtoIn;
import com.bank.models.User;
import com.bank.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity <User> createUser(@RequestBody @Valid UserDtoIn userDtoIn) throws DateTimeException {
            return ResponseEntity.ok(userService.createUser(userDtoIn));
    }

    //@GetMapping()
    //public List<UserDtoOut> getAllUsers() {

    //}
}
