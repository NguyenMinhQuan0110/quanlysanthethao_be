package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
	
	@Value("${app.jwt.refreshExpirationMs}")
    private long jwtRefreshExpirationMs;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Transactional
	public LoginResponse login(LoginRequest request) {
		User user= userRepository.findByEmail(request.getEmail());
		if(user==null) {
			throw new ApiException("Email không tồn tại");
		}else {
			if(!passwordEncoder.matches(request.getPassWord(), user.getPassword())) {
				throw new ApiException("Sai mật khẩu");
			}
			String message="Đăng nhập thành công";
			String token = jwtTokenProvider.generateToken(user.getEmail(),user.getId());
			String refreshToken= jwtTokenProvider.generateRefreshToken(user.getId(),user.getEmail());
			
			RefreshToken newreRefreshToken =new RefreshToken();
			newreRefreshToken.setToken(refreshToken);
			newreRefreshToken.setCreatedAt(LocalDateTime.now());
			newreRefreshToken.setUser(user);
			newreRefreshToken.setExpirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs));
			
			newreRefreshToken= refreshTokenRepository.save(newreRefreshToken);
			
			return  convertToResponse(message, token, refreshToken);
		}
	}
	
	public LoginResponse refreshToken(RefreshTokenRequest request) {
		RefreshToken refreshToken= refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(()->new ApiException("Refresh Token không tồn tại"));
		if(refreshToken.getExpirationTime().before(new Date())) {
			throw new ApiException("Refresh Token đã hết hạn");
		}
		User user =refreshToken.getUser();
		
		String newToken = jwtTokenProvider.generateToken(user.getEmail(),user.getId());
		
		return convertToResponse("Tạo Token mới thành công", newToken, request.getRefreshToken());	
	}
	
	@Transactional
	public void logout(String token) {
	    Long userId = jwtTokenProvider.getUserIdFromToken(token);

	    // Xóa refresh token của user khỏi DB
	    refreshTokenRepository.deleteByUserId(userId);
	}

	
	private LoginResponse convertToResponse(String message, String token, String refreshToken) {
		LoginResponse response = new LoginResponse();
		response.setMessage(message);
		response.setToken(token);
		response.setRefreshToken(refreshToken);
		return response;
	}
}
