package com.bank.repository;

import com.bank.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLogin(String login);

    boolean existsByPhones(String phone);

    boolean existsByEmails(String email);

    Optional<User> findByLogin(String login);
   // Optional<User> findByEmails(String email);

}
