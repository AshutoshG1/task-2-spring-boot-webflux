package com.techieAshutosh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String userRole;
}