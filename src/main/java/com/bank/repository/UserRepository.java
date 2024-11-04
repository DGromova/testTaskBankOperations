package com.bank.repository;

import com.bank.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLogin(String login);

 //   @Query("SELECT e FROM User u JOIN u.emails e")
   // List<String> findAllEmails();
}
