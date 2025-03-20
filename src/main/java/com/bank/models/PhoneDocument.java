package com.bank.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
public class PhoneDocument {
    @Field(type = FieldType.Keyword)
    private String phone;

    public PhoneDocument(String phone) {
        this.phone = phone;
    }

}
