package com.example.demo.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleToUserRequest {
	@NotNull(message = "UserId bị trống")
	private Long userId;
	@NotEmpty(message = "Danh sách RoleId bị trống")
	private List<Long> listIdRole;
}
