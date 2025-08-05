package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
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

	public LoginResponse login(LoginRequest request) {
	    User user = userRepository.findByEmail(request.getEmail());

	    if (user == null) {
	        throw new ApiException("Email không tồn tại");
	    }

	    // Nếu đang bị khóa
	    if (Boolean.TRUE.equals(user.getLocked())) {
	        // nếu còn thời gian bị khóa
	        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
	            throw new ApiException("Tài khoản đang bị khóa tới " + user.getLockedUntil());
	        } else {
	            // hết thời gian khóa => mở khóa lại
	            user.setLocked(false);
	            user.setLockedUntil(null);
	            user.setLoginFalse(0); // reset số lần sai
	            userRepository.save(user);
	        }
	    }

	    // Sai mật khẩu
	    if (!passwordEncoder.matches(request.getPassWord(), user.getPassword())) {
	        int count = (user.getLoginFalse() == null) ? 0 : user.getLoginFalse();
	        count++;
	        user.setLoginFalse(count);

	        // Nếu sai >5 lần thì khóa 30 phút
	        if (count > 5) {
	            user.setLocked(true);
	            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
	            user.setLoginFalse(0); // reset bộ đếm sau khi khóa
	        }

	        userRepository.save(user);
	        throw new ApiException("Sai mật khẩu. Tài khoản của bạn sẽ bị khóa 30 phút nếu đăng nhập sai quá 5 lần. Bạn còn "+(6-count)+" lần");
	    }

	    // Đăng nhập thành công => reset
	    user.setLoginFalse(0);
	    user.setLocked(false);
	    user.setLockedUntil(null);
	    userRepository.save(user);
//	    Xóa refressh token khi đăng nhập lại
	    refreshTokenRepository.deleteByUserId(user.getId());
	    // Tạo token & refresh token
	    String message = "Đăng nhập thành công";
	    String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());
	    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

	    RefreshToken newRefreshToken = new RefreshToken();
	    newRefreshToken.setToken(refreshToken);
	    newRefreshToken.setCreatedAt(LocalDateTime.now());
	    newRefreshToken.setUser(user);
	    newRefreshToken.setExpirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs));

	    refreshTokenRepository.save(newRefreshToken);

	    return convertToResponse(message, token, refreshToken);
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
	
	public UserResponse register(RegisterRequest request){
		User newUser = new User();
		newUser.setFullname(request.getFullname());
		newUser.setEmail(request.getEmail());
		User checkEmail = userRepository.findByEmail(request.getEmail());
		newUser.setPhoneNumber(request.getPhoneNumber());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		newUser.setCreateBy(request.getEmail());
		newUser.setCreateTime(LocalDateTime.now());
		newUser.setLoginFalse(0);
		newUser.setLocked(false);
		if(checkEmail!=null && checkEmail.getEmail().equals(request.getEmail())) {
			throw new ApiException("Email đã tồn tại");
		}
		if(!request.getPassword().equals(request.getPasswordConfirm())) {
			throw new ApiException("Mật khẩu không khớp");
		}
		newUser=userRepository.save(newUser);
		return convertToResponse(newUser);
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
