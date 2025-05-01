package com.bank.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Document(indexName = "users")
public class UserDocument {

    @Id
    private Integer id;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate birthdate;

    @Field(type = FieldType.Text)
    private String fullName;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<String> phones;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<String> emails;

    public UserDocument(User user) {
        this.id = user.getId();
        this.birthdate = user.getBirthdate();
        this.fullName = (
                user.getSurname() + " " +
                user.getName() + " " +
                Objects.toString(user.getMiddleName(), "")
        ).stripTrailing();
        this.phones = new HashSet<>(user.getPhones());
        this.emails = new HashSet<>(user.getEmails());
    }
}

