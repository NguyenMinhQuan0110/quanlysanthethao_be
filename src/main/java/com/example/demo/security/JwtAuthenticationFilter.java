package com.example.demo.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private RolePermissionRepository rolePermissionRepository;

	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException{
		try {
            String token = getJwtFromRequest(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                Long userId = tokenProvider.getUserIdFromToken(token);
                User user = userRepository.findById(userId).orElseThrow();
                if (user != null) {
                	Set<GrantedAuthority> authorities = new HashSet<>();

                    // lấy list role theo user
                    List<Role> roles = userRoleRepository.getRolesByUserId(userId);

                    // duyệt từng role → lấy permission của role
                    for (Role role : roles) {
                        List<Permission> perms = rolePermissionRepository.getPermissionsByRoleId(role.getId());
                        for (Permission p : perms) {
                            authorities.add(new SimpleGrantedAuthority(p.getName()));
                        }
                    }
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities); // bạn có thể thêm roles ở đây
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Đặt thông tin xác thực vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"timestamp\": \"" + LocalDateTime.now() + "\", " +
                "\"message\": \"" + ex.getMessage() + "\", " +
                "\"path\": \"" + request.getRequestURI() + "\" }");
            return;
        }

        filterChain.doFilter(request, response);
	}
	
	
	private String getJwtFromRequest(HttpServletRequest request) {
	        String bearerToken = request.getHeader("Authorization");
	        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	            return bearerToken.substring(7); // bỏ "Bearer "
	        }
	        return null;
	    }

}
