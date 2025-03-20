package com.bank.controller;

import com.bank.dto.UserDtoIn;
import com.bank.exception.ArgumentValidationException;
import com.bank.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello!");
    }

    @PostMapping("/create")
    public ResponseEntity <?> createUser(@Valid @RequestBody UserDtoIn userDtoIn, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
           Set<String> messages = bindingResult.getAllErrors().stream()
                   .map(DefaultMessageSourceResolvable::getDefaultMessage)
                   .collect(Collectors.toSet());
            throw new ArgumentValidationException(messages.toString());
        }
        if (userService.createUser(userDtoIn)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("New user created");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/add/phone")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPhone(@RequestParam String phone) {
        if (userService.addPhone(phone)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Phone number successfully added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/add/email")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addEmail(@RequestParam String email) {
        if (userService.addEmail(email)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email successfully added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/change/phone")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePhone(@RequestParam String oldPhone, @RequestParam String newPhone) {
        if (userService.changePhone(oldPhone, newPhone)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Phone number successfully changed");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/change/email")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeEmail(@RequestParam String oldEmail, @RequestParam String newEmail) {
        if (userService.changeEmail(oldEmail, newEmail)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email successfully changed");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/phone")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> deletePhone(@RequestParam String phone) {
        if (userService.deletePhone(phone)) {
            return ResponseEntity.status(HttpStatus.OK).body("Phone number successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/email")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> deleteEmail(@RequestParam String email) {
        if (userService.deleteEmail(email)) {
            return ResponseEntity.status(HttpStatus.OK).body("Email successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

       /* @GetMapping("/{email}")
    public ResponseEntity<Optional<User>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok()
                .body(userService.getUserByEmail(email));
    }*/

    @GetMapping("/find")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> findUser(@RequestParam(required = false) String birthdate,
                                            @RequestParam(required = false) String phone,
                                            @RequestParam(required = false) String fullName,
                                            @RequestParam(required = false) String email,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
      //  if (userService.findUsers()) {
     //   } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      //  }
    }

}
