package com.auth.authService.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.authService.Entitiy.UserRegister;

public interface RegisterUser extends JpaRepository<UserRegister,Long> {

}
