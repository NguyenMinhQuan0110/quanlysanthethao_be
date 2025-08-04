package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AssignRoleToUserRequest;
import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional
	public Page<UserResponse> getAllUser(Pageable pageable){
		Page<User> page = userRepository.findAll(pageable);
		return page.map(this::convertToResponse);
	}
	
	public UserResponse getUserById(Long id) {
		User findUser = userRepository.findById(id).orElseThrow(()-> new ApiException("User không tồn tại"));
		return convertToResponse(findUser);
	}
	
	public Page<UserResponse> seachUser (String keyword,Pageable pageable){
		Page<User> listUserSearch =userRepository.searchByNameOrEmailOrPhoneNumber(keyword,pageable);
		
		return listUserSearch.map(this::convertToResponse);
	}
	
	@Transactional
	public UserResponse create(CreateUserRequest userRequest) {
		
		User newUser = new User();
		newUser.setFullname(userRequest.getFullname());
		newUser.setEmail(userRequest.getEmail());
		User checkEmail = userRepository.findByEmail(userRequest.getEmail());
		newUser.setPhoneNumber(userRequest.getPhoneNumber());
		newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		newUser.setCreateBy("system");
		newUser.setCreateTime(LocalDateTime.now());
		if (!userRequest.getPassword().equals(userRequest.getConfirmPassword())) {
			throw new ApiException("Mật khẩu không khớp");
		}
		if(checkEmail!=null && checkEmail.getEmail().equals(userRequest.getEmail())) {
			throw new ApiException("Email đã tồn tại");
		}
		newUser=userRepository.save(newUser);
		return convertToResponse(newUser);
	}
	
	@Transactional
	public UserResponse update(UpdateUserRequest request) {
		
		User updateUser = userRepository.findById(request.getId()).orElseThrow(()-> new ApiException("User không tồn tại"));
		updateUser.setFullname(request.getFullname());
		updateUser.setEmail(request.getEmail());
		updateUser.setPhoneNumber(request.getPhoneNumber());
		updateUser.setUpdateBy("system");
		updateUser.setUpdateTime(LocalDateTime.now());
		
		updateUser=userRepository.save(updateUser);
		
		return convertToResponse(updateUser);
		
	}
	
	@Transactional
	public UserResponse assignRolesToUser(AssignRoleToUserRequest request) {
		 User user = userRepository.findById(request.getUserId())
			        .orElseThrow(() -> new ApiException("User không tồn tại"));
		 // Xóa hết roles cũ -> Hibernate sẽ tự delete ở bảng user_role vì orphanRemoval = true
		 if (user.getListRole() != null) {
			 user.getListRole().clear();
		 }
		// Gán quyền mới
	    List<UserRole> newUserRoles = new ArrayList<>();
	    for (Long roleId : request.getListIdRole()) {
	        Role role = roleRepository.findById(roleId)
	            .orElseThrow(() -> new ApiException("Role không tồn tại: " + roleId));
	        
	        UserRole userRole = new UserRole();
	        userRole.setUser(user);
	        userRole.setRole(role);
	        userRole.setCreateBy("system");
	        userRole.setCreateTime(LocalDateTime.now());
	        newUserRoles.add(userRole);
	    }

	    user.getListRole().addAll(newUserRoles);
	    userRepository.save(user); // cascade sẽ lưu UserRole

	    return convertToResponse(user);
	}
	
	@Transactional
	public void delete(Long id) {
		userRepository.deleteById(id);
	}
	
	private UserResponse convertToResponse(User user) {
		UserResponse userResponse= new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setFullname(user.getFullname());
		userResponse.setEmail(user.getEmail());
		userResponse.setPhoneNumber(user.getPhoneNumber());
		userResponse.setCreateBy(user.getCreateBy());
		userResponse.setCreateTime(user.getCreateTime());
		userResponse.setUpdateBy(user.getUpdateBy());
		userResponse.setUpdateTime(user.getUpdateTime());
		// xử lý danh sách Role
	    List<RoleResponse> roles = new ArrayList<>();
	    if (user.getListRole() != null) {
	        user.getListRole().forEach(userRole -> {
	            Role role = userRole.getRole();
	            if (role != null) {
	                RoleResponse roleResponse = new RoleResponse();
	                roleResponse.setId(role.getId());
	                roleResponse.setName(role.getName());
	                roleResponse.setDescription(role.getDescription());
	                roleResponse.setCreateBy(role.getCreateBy());
	                roleResponse.setCreateTime(role.getCreateTime());
	                roleResponse.setUpdateBy(role.getUpdateBy());
	                roleResponse.setUpdateTime(role.getUpdateTime());
	                roles.add(roleResponse);
	            }
	        });
	    }

	    userResponse.setRoles(roles);
		return userResponse;
	}
}
