package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.AssignRoleToUserRequest;
import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PreAuthorize("hasAuthority('search_user')")
	@GetMapping("/paginate")
	public ResponseEntity<Page<UserResponse>> getAllUser(@RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size", defaultValue = "10") int size ){
		Pageable pageable = PageRequest.of(page, size);
		Page<UserResponse> listUser =userService.getAllUser(pageable);
		return ResponseEntity.ok(listUser);
	}
	
	@PreAuthorize("hasAuthority('search_user')")
	@GetMapping("/search")
	public ResponseEntity<Page<UserResponse>> searchUser(@RequestParam("keyword") String keyword,@RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size", defaultValue = "10") int size){
		Pageable pageable = PageRequest.of(page, size);
		Page<UserResponse> listUserSearch= userService.seachUser(keyword,pageable);
		return ResponseEntity.ok(listUserSearch);
	}
	
	@PreAuthorize("hasAuthority('search_user')")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id){
		UserResponse findUser=userService.getUserById(id);
		return ResponseEntity.ok(findUser);
	}
	
	@PreAuthorize("hasAuthority('create_user')")
	@PostMapping("/create")
	public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest userRequest){
		UserResponse newUser= userService.create(userRequest);
		return ResponseEntity.ok(newUser);
	}
	
	@PreAuthorize("hasAuthority('update_user')")
	@PutMapping("/update")
	public ResponseEntity<UserResponse> update(@RequestBody UpdateUserRequest request){
		UserResponse updateUser=userService.update(request);
		return ResponseEntity.ok(updateUser);
	}
	
	@PreAuthorize("hasAuthority('assign_role')")
	@PostMapping("/assign-roles")
	public ResponseEntity<UserResponse> assignRoles(@RequestBody AssignRoleToUserRequest request) {
	    UserResponse updatedUser = userService.assignRolesToUser(request);
	    return ResponseEntity.ok(updatedUser);
	}
	
	@PreAuthorize("hasAuthority('delete_user')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Map<String, Object>>  delete(@PathVariable("id") Long id){
		userService.delete(id);
		Map<String, Object> response = new HashMap<>();
        response.put("message", "Xóa Người dùng thành công");
        return ResponseEntity.ok(response);
	}
	
 }
