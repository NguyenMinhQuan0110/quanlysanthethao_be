package com.example.demo.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
	
	private Long id;
	
	private String fullname;
	
    private String email;

    private String phoneNumber;

}
