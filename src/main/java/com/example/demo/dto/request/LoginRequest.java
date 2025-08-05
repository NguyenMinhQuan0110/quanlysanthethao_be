package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
	@NotBlank(message = "Bạn không được bỏ trống email")
	private String email;
	@NotBlank(message = "bạn phải điền mật khẩu")
	private String passWord;
	
}
