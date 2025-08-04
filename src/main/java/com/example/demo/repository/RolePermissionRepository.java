package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Permission;
import com.example.demo.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer>{
	@Modifying
	@Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
	void deleteByRoleId(@Param("roleId") Long roleId);
	
	@Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId")
	List<Permission> getPermissionsByRoleId(@Param("roleId") Long roleId);


}
