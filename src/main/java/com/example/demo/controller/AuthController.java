package com.example.demo.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
		LoginResponse userLogin= authService.login(request);
		return ResponseEntity.ok(userLogin);
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
		UserResponse newUser= authService.register(request);
		return ResponseEntity.ok(newUser);
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request){
		LoginResponse newToken= authService.refreshToken(request);
		return ResponseEntity.ok(newToken);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
	    String token = getJwtFromRequest(request);
	    Map<String, Object> response = new HashMap<String, Object>();
	    if (token == null) {
	    	 response.put("mesage", "Token không hợp lệ");
	        return ResponseEntity.badRequest().body(response);
	    }

	    authService.logout(token);
	    response.put("mesage", "đăng xuất thành công");
	    return ResponseEntity.ok(response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
	    String bearerToken = request.getHeader("Authorization");
	    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	        return bearerToken.substring(7);
	    }
	    return null;
	}

}
