package com.example.demo.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class AssignPermissionToRole {
	private Long roleId;
	private List<Long> listPermissionId;
}
