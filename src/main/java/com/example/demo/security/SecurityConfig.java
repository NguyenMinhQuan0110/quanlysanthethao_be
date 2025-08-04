package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {
	
	@Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
		http
		.csrf(csrf -> csrf.disable())
		.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/register").permitAll() // cho phép register/login không cần token
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/forgetpassword").permitAll()
                .requestMatchers("/api/auth/refresh-token").permitAll()
				.anyRequest()
//				.permitAll()
				.authenticated() // Các request khác cần xác thực
		);
		
		// Thêm JWT Filter vào trước filter UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	// Cần nếu bạn dùng AuthenticationManager cho đăng nhập (dùng ở bước login)
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
}
