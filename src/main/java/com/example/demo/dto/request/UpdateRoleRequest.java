package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleRequest {
	@NotBlank(message = "Trường id không được trống")
	private Long id;
	@NotBlank(message = "Trường name không được trống")
	private String name;
	@NotBlank(message = "Trường description không được trống")
	private String description;
}
