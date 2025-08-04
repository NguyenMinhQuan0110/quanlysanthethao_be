package com.example.demo.service;

//import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//import com.example.demo.dto.request.CreatePermissionRequest;
//import com.example.demo.dto.request.UpdatePermissionRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.entity.Permission;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.PermissionRepository;
//import jakarta.transaction.Transactional;

@Service
public class PermissionService {
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	public Page<PermissionResponse> getPermissionAll(Pageable pageable){
		Page<Permission> page= permissionRepository.findAll(pageable);
		return page.map(this::convertToResponse);
	}
	
	public Page<PermissionResponse> search(String keyword, Pageable pageable){
		Page<Permission> listPermission =permissionRepository.searchByNameOrDescription(keyword, pageable);
		return listPermission.map(this::convertToResponse);
	}
	
	public PermissionResponse getPermissionById(Long id) {
		Permission permission= permissionRepository.findById(id).orElseThrow(()->new ApiException("Permission không tồn tại"));
		return convertToResponse(permission);
	}
	
//	@Transactional
//	public PermissionResponse create(CreatePermissionRequest request) {
//		Permission checkPermission = permissionRepository.findByName(request.getName());
//		if(checkPermission!=null) {
//			throw new ApiException("Permission đã tồn tại");
//		}
//		Permission newPermission = new Permission();
//		newPermission.setName(request.getName());
//		newPermission.setDescription(request.getDescription());
//		newPermission.setCreateBy("system");
//		newPermission.setCreateTime(LocalDateTime.now());
//		newPermission=permissionRepository.save(newPermission);
//		return convertToResponse(newPermission);
//	}
//	
//	@Transactional
//	public PermissionResponse update(UpdatePermissionRequest request) {
//		Permission updatePermission =permissionRepository.findById(request.getId()).orElseThrow(()-> new ApiException("Permission không tồn tại"));
//		Permission checkPermission = permissionRepository.findByName(request.getName());
//		if(checkPermission!=null) {
//			throw new ApiException("Permission đã tồn tại");
//		}
//		updatePermission.setName(request.getName());
//		updatePermission.setDescription(request.getDescription());
//		updatePermission.setUpdateBy("system");
//		updatePermission.setUpdateTime(LocalDateTime.now());
//		updatePermission=permissionRepository.save(updatePermission);
//		return convertToResponse(updatePermission);
//	}
	
	
//	@Transactional
//	public void delete(Long id) {
//		permissionRepository.deleteById(id);
//	}
	
	private PermissionResponse convertToResponse(Permission permission) {
		PermissionResponse permissionResponse = new PermissionResponse();
		permissionResponse.setId(permission.getId());
		permissionResponse.setName(permission.getName());
		permissionResponse.setDescription(permission.getDescription());
		permissionResponse.setCreateBy(permission.getCreateBy());
		permissionResponse.setCreateTime(permission.getCreateTime());
		permissionResponse.setUpdateBy(permission.getUpdateBy());
		permissionResponse.setUpdateTime(permission.getUpdateTime());
		return permissionResponse;
	}
}
