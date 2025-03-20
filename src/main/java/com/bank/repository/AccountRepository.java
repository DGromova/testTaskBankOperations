package com.bank.repository;

import com.bank.models.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM ACCOUNTS WHERE USER_ID = :userId", nativeQuery = true)
    Optional<Account> findAccountByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query(value = "UPDATE ACCOUNTS SET BALANCE = :balance WHERE USER_ID = :userId", nativeQuery = true)
    void updateBalanceByUserId(@Param("userId") Integer userId, @Param("balance") BigDecimal balance);


}
