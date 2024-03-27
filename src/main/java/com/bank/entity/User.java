package com.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "phone_number", nullable = false)
    private String[] phoneNumber;
    @Column(nullable = false)
    private String[] email;
    @Column(columnDefinition = "money default 100.0")
    private double balance;
    @Column(nullable = false)
    private Date birthdate;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String name;
    @Column(name = "middle_name")
    private String middleName;
    @Column(unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Transient
    private String passwordConfirm;
    @Enumerated(EnumType.STRING)
    private Role role;



}
