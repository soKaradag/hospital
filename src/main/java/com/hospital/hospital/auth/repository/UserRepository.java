package com.hospital.hospital.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.auth.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	Page<User> findAllByOrderByUsernameAsc(Pageable pageable);
}
