package com.bank.repository;

import com.bank.models.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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


    //to transfer money
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM ACCOUNTS WHERE USER_ID = :userId", nativeQuery = true)
    Optional<Account> findAccountByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE ACCOUNTS SET CURRENT_BALANCE = :currentBalance WHERE USER_ID = :userId", nativeQuery = true)
    void updateCurrentBalanceByUserId(@Param("userId") Long userId, @Param("currentBalance") BigDecimal currentBalance);


    //for interest accrual
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Slice<Account> findAllBy(Pageable page);

    //@Modifying
    //@Query(value = "UPDATE ACCOUNTS SET CURRENT_BALANCE = :currentBalance WHERE ID = :id", nativeQuery = true)
    //void updateCurrentBalanceById(@Param("id") Long id, @Param("currentBalance") BigDecimal currentBalance);



}
