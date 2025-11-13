package com.auth.authService.Dao;	


import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.authService.Entitiy.UserVerification;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserVerification, Long> {
	Optional<UserVerification> findByEmail(String email);

}
