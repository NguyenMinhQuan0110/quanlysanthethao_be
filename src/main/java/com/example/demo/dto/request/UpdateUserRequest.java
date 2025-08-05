package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
	@NotNull(message = "Trường id không được trống")
	private Long id;
	@NotBlank(message = "Trường fullname không được trống")
	private String fullname;
	@NotBlank(message = "Trường email không được trống")
    private String email;
	@NotBlank(message = "Trường phoneNumber không được trống")
	@Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ, phải gồm 10 chữ số và bắt đầu bằng 0")
    private String phoneNumber;
	@NotBlank(message = "Trường passwordOld không được trống")
    private String password;

}
