package com.consiti.springsecurity1.security.repository;

import com.consiti.springsecurity1.security.entity.Role;
import com.consiti.springsecurity1.security.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(RoleName roleName);
}
