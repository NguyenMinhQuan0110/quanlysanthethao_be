package com.example.demo.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class AssignRoleToUserRequest {
	private Long userId;
	private List<Long> listIdRole;
}
