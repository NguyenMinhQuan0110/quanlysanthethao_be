package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
	@NotBlank(message = "Trường fullname không được trống")
	private String fullname;
	@NotBlank(message = "Trường email không được trống")
	@Email
    private String email;
	@NotBlank(message = "Trường phoneNumber không được trống")
	@Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ, phải gồm 10 chữ số và bắt đầu bằng 0")
    private String phoneNumber;
	@NotBlank(message = "Trường password không được trống")
	@Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
	@Pattern(
	    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
	    message = "Mật khẩu phải chứa chữ thường, chữ hoa, số và ký tự đặc biệt"
	)
    private String password;
	@NotBlank(message = "Trường passwordConfirm không được trống")
    private String passwordConfirm;
}
