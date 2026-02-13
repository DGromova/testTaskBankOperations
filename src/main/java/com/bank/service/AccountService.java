package com.bank.service;

import com.bank.dto.Transaction;

public interface AccountService {

    boolean transfer(Transaction transaction);

}
