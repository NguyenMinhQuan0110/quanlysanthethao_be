package com.example.demo.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PermissionResponse {
	
	private Long id;
	
	private String name;
	
	private String description;
	
	private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;
}
