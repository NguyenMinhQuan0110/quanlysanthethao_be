package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
	@NotBlank(message = "Trường name không được trống")
	private String name;
	@NotBlank(message = "Trường description không được trống")
	private String description;
}
