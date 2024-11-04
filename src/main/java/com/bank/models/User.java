package com.bank.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

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
    @Column(unique = true, nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDate birthdate;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String name;
    @Column(name = "middle_name")
    private String middleName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;

    @ElementCollection
    @CollectionTable(name = "user_phones", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phones", nullable = false)
    private Set<String> phones;

    @ElementCollection
    @CollectionTable(name = "user_emails", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "emails", nullable = false)
    private Set<String> emails;
}
