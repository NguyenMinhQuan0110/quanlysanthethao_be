package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>{
	
	Page<Permission> findAll(Pageable pageable);
	
	@Query("SELECT p FROM Permission p WHERE p.name = :name")
	Permission findByName(@Param("name") String name);
	
	@Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Permission> searchByNameOrDescription(@Param("keyword") String keyword, Pageable pageable);
}
