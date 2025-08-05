package com.example.demo.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPermissionToRole {
	@NotNull(message = "RoleId bị trống")
	private Long roleId;
	@NotEmpty(message = "Danh sách PermissionId bị trống")
	private List<Long> listPermissionId;
}
