package com.example.demo.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.email = :email")
	User findByEmail(@Param("email") String email);
		
//	@Query("SELECT u FROM User u WHERE LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//  List<User> searchByNameOrEmailOrPhoneNumber(@Param("keyword") String keyword);
	
//	Phân trang
	
	// Phân trang toàn bộ user)
	Page<User> findAll(Pageable pageable);
	
	// Phân trang tìm kiếm
	@Query("SELECT u FROM User u WHERE LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<User> searchByNameOrEmailOrPhoneNumber(@Param("keyword") String keyword, Pageable pageable);


}
