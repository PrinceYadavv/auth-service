package com.auth.authService.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth.authService.Entitiy.UserRegister;
import com.auth.authService.JwtService.JwtUtil;
import com.auth.authService.Service.OtpService;
import com.auth.authService.Service.RegisterService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class authController {

	@Autowired
	private OtpService otpService;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RegisterService registerService;

	@PostMapping("/request-otp")
	public ResponseEntity<String> requestOtp(@RequestParam String email, 
	                                         @RequestParam(required = false) String name, 
	                                         @RequestParam(required = false) String phone) {
	    // Step 1: Check if user exists
	    boolean exists = registerService.existsByEmail(email);

	    if (!exists) {
	        // Step 2: Register new user
	        if (name == null || phone == null) {
	            return ResponseEntity.badRequest().body("New user must provide name and phone number");
	        }
	        UserRegister newUser = new UserRegister(email, name, phone);
	        registerService.saveUser(newUser);
	    }

	    // Step 3: Send OTP
	    otpService.requestOtp(email);
	    String message = exists 
	        ? "Existing user: OTP sent successfully to " + email 
	        : "New user registered. OTP sent successfully to " + email;

	    return ResponseEntity.ok(message);
	}


	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp, HttpServletResponse response) {
	    boolean valid = otpService.verifyOtp(email, otp);
	    if (!valid) {
	        return ResponseEntity.badRequest().body("Invalid or expired OTP");
	    }

	    String token = jwtUtil.generateToken(email);

	    // Store JWT securely in HttpOnly cookie
	    Cookie jwtCookie = new Cookie("jwt", token);
	    jwtCookie.setHttpOnly(true);          // prevents JS access (security)
	    jwtCookie.setSecure(false);           // set true if using HTTPS
	    jwtCookie.setPath("/");               // accessible for all paths
	    jwtCookie.setMaxAge(60 * 60);         // expires in 1 hour
	    response.addCookie(jwtCookie);
	    return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestParam String email,@RequestParam String name,@RequestParam String phone){
		UserRegister user =new UserRegister(email,name,phone);
	    String result=	registerService.saveUser(user);
		return ResponseEntity.ok(result);
	}
	
	

}
