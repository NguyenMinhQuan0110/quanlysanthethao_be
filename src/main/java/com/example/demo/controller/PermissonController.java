package com.example.demo.controller;

//import java.util.HashMap;
//import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.example.demo.dto.request.CreatePermissionRequest;
//import com.example.demo.dto.request.UpdatePermissionRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.service.PermissionService;

@RestController
@RequestMapping("api/permission")
public class PermissonController {
	
	@Autowired
	private PermissionService permissionService;
	
	@GetMapping
	public ResponseEntity<Page<PermissionResponse>> getAllRole(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size){
		Pageable pageable= PageRequest.of(page, size);
		Page<PermissionResponse> listPermission = permissionService.getPermissionAll(pageable);
		return ResponseEntity.ok(listPermission);
	}
	
	@GetMapping("/search")
	public ResponseEntity<Page<PermissionResponse>> search(@RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size",defaultValue = "10") int size,@RequestParam(name = "keyword") String keyword){
		Pageable pageable= PageRequest.of(page, size);
		Page<PermissionResponse> listPermission = permissionService.search(keyword, pageable);
		return ResponseEntity.ok(listPermission);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PermissionResponse> getRoleById(@PathVariable("id") Long id){
		PermissionResponse permission = permissionService.getPermissionById(id);
		return ResponseEntity.ok(permission);
	}
	
//	@PostMapping("/create")
//	public ResponseEntity<PermissionResponse> create (@RequestBody CreatePermissionRequest request){
//		PermissionResponse newPermission = permissionService.create(request);
//		return ResponseEntity.ok(newPermission);
//	}
//	
//	@PutMapping("/update")
//	public ResponseEntity<PermissionResponse>update (@RequestBody UpdatePermissionRequest request){
//		PermissionResponse updatePermission =permissionService.update(request);
//		return ResponseEntity.ok(updatePermission);
//	}
//	
//	@DeleteMapping("delete/{id}")
//	public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Long id){
//		permissionService.delete(id);
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Xóa Permission dùng thành công");
//        return ResponseEntity.ok(response);
//	}
}
