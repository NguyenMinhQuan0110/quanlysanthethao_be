package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;


import lombok.Data;

@Data
public class RoleResponse {
	
	private Long id;
	
	private String name;
	
	private String description;
	
	private List<PermissionResponse> listPermission;
	
	private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;
}
