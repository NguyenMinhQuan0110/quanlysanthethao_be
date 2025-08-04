package com.example.demo.dto.request;


import lombok.Data;

@Data
public class CreateUserRequest {
	
	private String fullname;
	
    private String email;

    private String phoneNumber;

    private String password;
    
    private String confirmPassword;

}
