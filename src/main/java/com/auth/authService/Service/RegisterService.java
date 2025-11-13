package com.auth.authService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.authService.Dao.RegisterUser;
import com.auth.authService.Dao.UserRepository;
import com.auth.authService.Entitiy.UserRegister;


@Service
public class RegisterService {

    @Autowired
    private RegisterUser registerUser;  // inject DAO interface
    @Autowired
    private UserRepository userRegister;
    

    public String saveUser(UserRegister user) {
        UserRegister savedUser = registerUser.save(user);
        return "User registered successfully with ID: " + savedUser.getId();
    }
    public boolean existsByEmail(String email) {
        return userRegister.findByEmail(email).isPresent();
    }

}
