package com.bank.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.bank.dto.UserDtoIn;
import com.bank.exception.ArgumentValidationException;
import com.bank.models.UserDocument;
import com.bank.models.UserSearchParameters;
import com.bank.service.UserElasticsearchService;
import com.bank.service.UserService;
import com.bank.util.ValidationUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


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
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when creating a new user: {}", exception.getMessage());
        }

        if (userService.createUser(userDtoIn)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("New user created");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/add/phone")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPhone(@Valid @RequestParam
                                          @Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
                                                  message = "The phone number must start with +7 or 8. For a landline phone, enter the area code")
                                          String phone,
                                      BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when adding a phone number: {}", exception.getMessage());
        }

        if (userService.addPhone(phone)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Phone number successfully added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/add/email")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addEmail(@Valid @RequestParam
                                          @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid email format")
                                          String email,
                                      BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when adding a email: {}", exception.getMessage());
        }

        if (userService.addEmail(email)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email successfully added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/change/phone")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePhone(@Valid @RequestParam
                                             @Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
                                                     message = "The phone number must start with +7 or 8. For a landline phone, enter the area code")
                                             String oldPhone,
                                         @Valid @RequestParam
                                             @Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
                                                     message = "The phone number must start with +7 or 8. For a landline phone, enter the area code")
                                             String newPhone,
                                         BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when changing a phone number: {}", exception.getMessage());
        }

        if (userService.changePhone(oldPhone, newPhone)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Phone number successfully changed");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/change/email")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeEmail(@Valid @RequestParam
                                             @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid email format")
                                             String oldEmail,
                                         @Valid @RequestParam
                                             @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid email format")
                                             String newEmail,
                                         BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when changing a email: {}", exception.getMessage());
        }

        if (userService.changeEmail(oldEmail, newEmail)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email successfully changed");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/phone")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> deletePhone(@Valid @RequestParam
                                  @Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
                                          message = "The phone number must start with +7 or 8. For a landline phone, enter the area code")
                                  String phone,
                                  BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when deleting a phone number: {}", exception.getMessage());
        }

        if (userService.deletePhone(phone)) {
            return ResponseEntity.status(HttpStatus.OK).body("Phone number successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/email")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> deleteEmail(@Valid @RequestParam
                                  @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid email format")
                                  String email,
                                  BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of parameter validation when deleting a email: {}", exception.getMessage());
        }

        if (userService.deleteEmail(email)) {
            return ResponseEntity.status(HttpStatus.OK).body("Email successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<SearchResponse<UserDocument>> findUser(@Valid @RequestParam(required = false)
                                                          @Pattern(regexp = "(\\d{2})\\.(\\d{2})\\.(19|20)\\d{2}",
                                                                  message = "Enter the birthdate in the format dd.MM.yyyy") String birthdate,

                                                          @Valid @RequestParam(required = false)
                                                          @Pattern(regexp = "^(8|\\+7)[\\- ]?(\\(?\\d{3,5}\\)?[\\- ]?)?[\\d\\- ]{5,10}$",
                                                                  message = "The phone number must start with +7 or 8. For a landline phone, enter the area code") String phone,

                                                          @Valid @RequestParam(required = false)
                                                          @Size(max = 150, message = "Full name is too long") String fullName,

                                                          @Valid @RequestParam(required = false)
                                                          @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                                                                  message = "Invalid email format") String email,

                                                          @RequestParam(required = false, defaultValue = "0")
                                                          int page,

                                                          @RequestParam(required = false, defaultValue = "10")
                                                          int size,

                                                          @RequestParam(required = false, defaultValue = "id")
                                                          String sortField,

                                                          @RequestParam(required = false, defaultValue = "asc")
                                                          String direction,

                                                          BindingResult bindingResult) {
        try {
            ValidationUtils.validateParameters(bindingResult);
        } catch (ArgumentValidationException exception) {
            log.info("Exception of validation of search parameters: {}", exception.getMessage());
        }

        UserSearchParameters userSearchParameters = UserSearchParameters.builder()
                .birthdate(birthdate).phone(phone).fullName(fullName).email(email).build();

        return ResponseEntity.ok(userElasticsearchService.findUsers(userSearchParameters, page, size, sortField, direction));
    }

}
