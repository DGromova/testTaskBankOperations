package com.bank.repository;

import com.bank.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findById(Integer id);

    boolean existsByLogin(String login);

    boolean existsByPhones(String phone);

    boolean existsByEmails(String email);

    Optional<User> findByLogin(String login);

    /*@Modifying
    @Query(value = "INSERT INTO USER_PHONES (USER_ID, PHONES) VALUES(:userId, :phone)", nativeQuery = true)
    void addPhoneByUserID(@Param("userId") Integer userId, @Param("phone") String phone);

    @Modifying
    @Query(value = "INSERT INTO USER_EMAILS (USER_ID, EMAILS) VALUES(:userId, :email)", nativeQuery = true)
    void addEmailByUserID(@Param("userId") Integer userId, @Param("email") String email);

    @Query(value = "SELECT PHONES FROM USER_PHONES WHERE USER_ID = :userId", nativeQuery = true)
    List<String> findPhonesByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT EMAILS FROM USER_EMAILS WHERE USER_ID = :userId", nativeQuery = true)
    List<String> findEmailsByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query(value = "DELETE FROM USER_PHONES WHERE PHONES = :phone", nativeQuery = true)
    void deletePhone(@Param("phone") String phone);

    @Modifying
    @Query(value = "DELETE FROM USER_EMAILS WHERE EMAILS = :email", nativeQuery = true)
    void deleteEmail(@Param("email") String email);*/

}
