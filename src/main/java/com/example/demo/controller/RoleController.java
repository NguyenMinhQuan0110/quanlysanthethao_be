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

import com.example.demo.dto.request.AssignPermissionToRole;
import com.example.demo.dto.request.CreateRoleRequest;
import com.example.demo.dto.request.UpdateRoleRequest;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.service.RoleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/role")
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@PreAuthorize("hasAuthority('view_role')")
	@GetMapping
	public ResponseEntity<Page<RoleResponse>> getAllRole(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size){
		Pageable pageable= PageRequest.of(page, size);
		Page<RoleResponse> listRole= roleService.getAllRole(pageable);
		return ResponseEntity.ok(listRole);
	}
	
	@PreAuthorize("hasAuthority('view_role')")
	@GetMapping("/search")
	public ResponseEntity<Page<RoleResponse>> search(@RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size",defaultValue = "10") int size,@RequestParam(name = "keyword") String keyword){
		Pageable pageable= PageRequest.of(page, size);
		Page<RoleResponse> listRole = roleService.search(keyword, pageable);
		return ResponseEntity.ok(listRole);
	}
	
	@PreAuthorize("hasAuthority('view_role')")
	@GetMapping("/{id}")
	public ResponseEntity<RoleResponse> getRoleById(@PathVariable("id") Long id){
		RoleResponse role = roleService.getRoleById(id);
		return ResponseEntity.ok(role);
	}
	
	@PreAuthorize("hasAuthority('create_role')")
	@PostMapping("/create")
	public ResponseEntity<RoleResponse> create (@Valid @RequestBody CreateRoleRequest request,HttpServletRequest httpServletRequest){
		String token = httpServletRequest.getHeader("Authorization");
		RoleResponse newRole = roleService.create(request,token.substring(7));
		return ResponseEntity.ok(newRole);
	}
	
	@PreAuthorize("hasAuthority('update_role')")
	@PutMapping("/update")
	public ResponseEntity<RoleResponse>update (@Valid @RequestBody UpdateRoleRequest request,HttpServletRequest httpServletRequest){
		String token = httpServletRequest.getHeader("Authorization");
		RoleResponse updateRole =roleService.update(request,token.substring(7));
		return ResponseEntity.ok(updateRole);
	}
	
	@PreAuthorize("hasAuthority('assig_permisstion_role')")
	@PostMapping("/assign-permission")
	public ResponseEntity<RoleResponse> assignRoles(@Valid @RequestBody AssignPermissionToRole request,HttpServletRequest httpServletRequest) {
		String token = httpServletRequest.getHeader("Authorization");
		RoleResponse updateRole = roleService.assignPermissionToRole(request,token.substring(7));
	    return ResponseEntity.ok(updateRole);
	} 
	
	@PreAuthorize("hasAuthority('delete_role')")
	@DeleteMapping("delete/{id}")
	public ResponseEntity<Map<String, Object>> delete(@Valid @PathVariable("id") Long id){
		roleService.delete(id);
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Xóa Quyền dùng thành công");
        return ResponseEntity.ok(response);
	}
}
