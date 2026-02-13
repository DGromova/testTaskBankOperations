package com.bank.service;

import com.bank.dto.UserDtoIn;

public interface UserService {

    boolean createUser(UserDtoIn userDtoIn);

    boolean addPhone(String phone);

    boolean addEmail(String email);

    boolean changePhone(String oldPhone, String newPhone);

    boolean changeEmail(String oldEmail, String newEmail);

    boolean deletePhone(String phone);

    boolean deleteEmail(String email);

}
