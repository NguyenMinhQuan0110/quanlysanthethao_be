package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Role;
import com.example.demo.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer>{
	@Modifying
	@Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId")
	void deleteByUserId(@Param("userId") Long userId);
	
	@Query("SELECT ur.role FROM UserRole ur WHERE ur.user.id = :userId")
	List<Role> getRolesByUserId(@Param("userId") Long userId);

}
