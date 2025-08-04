package com.example.demo.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {
	
	@Value("${app.jwt.secret}")
	private String JWT_SECRET;
	
	@Value("${app.jwt.expiration}")
	private Long JWT_EXPIRATION ;
	
	@Value("${app.jwt.refreshExpirationMs}")
    private long jwtRefreshExpirationMs;
	
	//tạo token 
	public String generateToken(String email, Long userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime()+ JWT_EXPIRATION);
		
		return Jwts.builder()
				.setSubject(userId.toString())
				.claim("email", email)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS256, JWT_SECRET)
				.compact();
	}
	
	 // Tạo Refresh Token
    public String generateRefreshToken(Long userId,String email) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }
    
 // Lấy userId từ token
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
	
	//lấy email từ token 
	public String getEmailFromToken(String token) {
		Claims claims = Jwts.parser()
	            .setSigningKey(JWT_SECRET)
	            .parseClaimsJws(token)
	            .getBody();// Lấy Claims
		return claims.get("email", String.class);// Lấy email từ claim
	}
	
	//xác thực token
	public boolean validateToken(String authToken) {
		try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
		} catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token hết hạn", ex);
        } catch (UnsupportedJwtException ex) {
            throw new RuntimeException("Token không hỗ trợ", ex);
        } catch (MalformedJwtException ex) {
            throw new RuntimeException("Token sai định dạng", ex);
        } catch (SignatureException ex) {
            throw new RuntimeException("Chữ ký không hợp lệ", ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Token rỗng", ex);
        }
	}
}
