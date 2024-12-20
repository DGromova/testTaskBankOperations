package com.bank.controller;

import com.bank.dto.UserDtoIn;
import com.bank.exception.ArgumentValidationException;
import com.bank.service.UserService;
//import jakarta.mvc.binding.BindingResult;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
//@Validated
@RestController
@RequestMapping("/bank/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

  //  @Validated
    @PostMapping("/create")
    public ResponseEntity <?> createUser(@Valid @RequestBody UserDtoIn userDtoIn, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
           Set<String> messages = bindingResult.getAllErrors().stream()
                   .map(DefaultMessageSourceResolvable::getDefaultMessage)
                   .collect(Collectors.toSet());
            throw new ArgumentValidationException(messages.toString());
        }
        if (userService.createUser(userDtoIn)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

/*    @PatchMapping("/phones/add")
    public ResponseEntity<?> addPhone(@RequestParam String phone) {
        if (userService.addPhone(phone)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }*/

   /* @GetMapping("/{email}")
    public ResponseEntity<Optional<User>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok()
                .body(userService.getUserByEmail(email));
    }*/
}
