package org.example.sciencesocialmedia.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
}
