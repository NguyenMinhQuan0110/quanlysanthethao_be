package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer>{
	@Modifying
	@Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
	void deleteByRoleId(@Param("roleId") Long roleId);
}
