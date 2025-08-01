package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AssignPermissionToRole;
import com.example.demo.dto.request.CreateRoleRequest;
import com.example.demo.dto.request.UpdateRoleRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RolePermission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	
	public Page<RoleResponse> getAllRole(Pageable pageable){
		Page<Role> page = roleRepository.findAll(pageable);
		return page.map(this::convertToResponse);
	}
	
	public Page<RoleResponse> search(String keyword, Pageable pageable){
		Page<Role> listRole =roleRepository.searchByNameOrDescription(keyword, pageable);
		return listRole.map(this::convertToResponse);
	}
	
	public RoleResponse getRoleById(Long id) {
		Role role= roleRepository.findById(id).orElseThrow(()->new RuntimeException("Role không tồn tại"));
		return convertToResponse(role);
	}
	
	@Transactional
	public RoleResponse create(CreateRoleRequest request) {
		Role newRole =new Role();
		Role checkrole = roleRepository.findByName(request.getName());
		if(checkrole!=null) {
			throw new RuntimeException("Quyền đã tồn tại");
		}
		newRole.setName(request.getName());
		newRole.setDescription(request.getDescription());
		newRole.setCreateBy("system");
		newRole.setCreateTime(LocalDateTime.now());
		newRole=roleRepository.save(newRole);
		return convertToResponse(newRole);
	}
	
	@Transactional
	public RoleResponse update(UpdateRoleRequest request) {
		Role updateRole =roleRepository.findById(request.getId()).orElseThrow(()-> new RuntimeException("Role không tồn tại"));
		Role checkrole = roleRepository.findByName(request.getName());
		if(checkrole!=null) {
			throw new RuntimeException("Quyền đã tồn tại");
		}
		updateRole.setName(request.getName());
		updateRole.setDescription(request.getDescription());
		updateRole.setUpdateBy("system");
		updateRole.setUpdateTime(LocalDateTime.now());
		updateRole=roleRepository.save(updateRole);
		return convertToResponse(updateRole);
	}
	
	@Transactional
	public RoleResponse assignPermissionToRole(AssignPermissionToRole request) {
		Role role = roleRepository.findById(request.getRoleId()).orElseThrow(()->new RuntimeException("Role không tồn tại"));
		
		rolePermissionRepository.deleteByRoleId(request.getRoleId());
		
		List<RolePermission> newRolePermission= new ArrayList<>();
		for(Long permissionId: request.getListPermissionId()) {
			Permission permission= permissionRepository.findById(permissionId).orElseThrow(()->new RuntimeException("Permission không tồn tại"));
			RolePermission rolePermission =new RolePermission();
			rolePermission.setRole(role);
			rolePermission.setPermission(permission);
			rolePermission.setCreateBy("system");
			rolePermission.setCreateTime(LocalDateTime.now());
			newRolePermission.add(rolePermission);
		}
		role.getListPermission().addAll(newRolePermission);
		roleRepository.save(role);
		return convertToResponse(role);
	}
	
	@Transactional
	public void delete(Long id) {
		roleRepository.deleteById(id);
	}
	
	private RoleResponse convertToResponse(Role role) {
		RoleResponse roleResponse = new RoleResponse();
		roleResponse.setId(role.getId());
		roleResponse.setName(role.getName());
		roleResponse.setDescription(role.getDescription());
		roleResponse.setCreateBy(role.getCreateBy());
		roleResponse.setCreateTime(role.getCreateTime());
		roleResponse.setUpdateBy(role.getUpdateBy());
		roleResponse.setUpdateTime(role.getUpdateTime());
		List<PermissionResponse> listPermission = new ArrayList<>();
		if(role.getListPermission()!=null) {
			role.getListPermission().forEach(rolePermission->{
				Permission permission= rolePermission.getPermission();
				if(permission!=null) {
					PermissionResponse permissionResponse=new PermissionResponse();
					permissionResponse.setId(role.getId());
					permissionResponse.setName(role.getName());
					permissionResponse.setDescription(role.getDescription());
					permissionResponse.setCreateBy(role.getCreateBy());
					permissionResponse.setCreateTime(role.getCreateTime());
					permissionResponse.setUpdateBy(role.getUpdateBy());
					permissionResponse.setUpdateTime(role.getUpdateTime());
					listPermission.add(permissionResponse);
				}
				
			});
		}
		roleResponse.setListPermission(listPermission);
		return roleResponse;
	}
}
