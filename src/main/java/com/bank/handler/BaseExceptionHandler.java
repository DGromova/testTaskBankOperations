package com.bank.handler;

import com.bank.exception.EmailAlreadyExistsException;
import com.bank.exception.LoginAlreadyExistsException;
import com.bank.exception.NotFoundException;
import com.bank.exception.PhoneAlreadyExistsException;
import com.bank.messages.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ErrorMessage> dateTimeException(DateTimeException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler({LoginAlreadyExistsException.class, EmailAlreadyExistsException.class, PhoneAlreadyExistsException.class})
    public ResponseEntity<ErrorMessage> dateAlreadyExistsException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(exception.getMessage()));
    }

}
