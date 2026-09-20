package com.pgfinder.user.dto;

import com.pgfinder.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {

    @NotBlank(message="Name is required")
    @Size(min = 3 ,max = 20,message = "Name must be 2 and 50 characters")
    private String name;

    @NotBlank
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6,message = "password must be 6 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 10,
            message = "Phone number must be 10 digits")
    private String phone;

    private Role role;

}
