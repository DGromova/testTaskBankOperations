package com.bank.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
public class EmailDocument {
    @Field(type = FieldType.Keyword)
    private String email;

    public EmailDocument(String email) {
        this.email = email;
    }

}
