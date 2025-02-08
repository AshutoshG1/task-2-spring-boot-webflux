package com.techieAshutosh.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private String userRole;
}