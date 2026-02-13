package com.bank.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private LocalDate birthdate;

    @Setter
    @Column(nullable = false)
    private String surname;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(name = "middle_name")
    private String middleName;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Account account;

    @Setter
    @ElementCollection
    @CollectionTable(name = "user_phones", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phones", nullable = false, unique = true)
    private Set<String> phones;

    @Setter
    @ElementCollection
    @CollectionTable(name = "user_emails", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "emails", nullable = false, unique = true)
    private Set<String> emails;

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

}
