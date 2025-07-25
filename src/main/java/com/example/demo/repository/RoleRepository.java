package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Page<Role> findAll(Pageable pageable);
	
	@Query("SELECT r FROM Role r WHERE r.name = :name")
	Role findByName(@Param("name") String name);
	
	@Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Role> searchByNameOrDescription(@Param("keyword") String keyword, Pageable pageable);
}
