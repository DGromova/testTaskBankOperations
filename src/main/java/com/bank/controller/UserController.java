package com.bank.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.bank.dto.FilterParams;
import com.bank.dto.UserDtoIn;
import com.bank.exception.ArgumentValidationException;
import com.bank.models.UserDocument;
import com.bank.service.UserService;
import com.bank.service.UserElasticsearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final UserElasticsearchService userElasticsearchService;

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

    @GetMapping("/find")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<SearchResponse<UserDocument>> findUser(@Valid @RequestParam(required = false)
                                              @Pattern(regexp = "(\\d{2})\\.(\\d{2})\\.(19|20)\\d{2}",
                                                      message = "Enter the birthdate in the format dd.MM.yyyy") String birthdate,

                                                          @Valid @RequestParam(required = false) @Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
                                                      message = "The phone number must start with +7 or 8. For a landline phone, enter the area code") String phone,

                                                          @Valid @RequestParam(required = false) @Size(max = 150, message = "Full name is too long") String fullName,

                                                          @Valid @RequestParam(required = false) @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                                                      message = "Invalid email format") String email,

                                                          @RequestParam(defaultValue = "0") int page,

                                                          @RequestParam(defaultValue = "10") int size,

                                                          @RequestParam(defaultValue = "id") String sortField,

                                                          @RequestParam(defaultValue = "asc") String direction,

                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Set<String> messages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toSet());
            throw new ArgumentValidationException(messages.toString());
        }

        if (birthdate == null & fullName == null & phone == null & email == null) {
            throw new ArgumentValidationException("No parameters are set");        }

        FilterParams filterParams = FilterParams.builder()
                .birthdate(birthdate)
                .phone(phone)
                .fullName(fullName)
                .email(email)
                .build();

        return ResponseEntity.ok(userElasticsearchService.findUsers(filterParams, page, size, sortField, direction));
    }

}
