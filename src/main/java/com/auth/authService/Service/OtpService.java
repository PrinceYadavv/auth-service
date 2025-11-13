package com.auth.authService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.auth.authService.Dao.UserRepository;
import com.auth.authService.Entitiy.UserVerification;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JavaMailSender mailSender;

    public void requestOtp(String email) {
        Optional<UserVerification> optionalUser = userRepo.findByEmail(email);
        UserVerification userVerification = optionalUser.orElseGet(() -> {
            UserVerification newUser = new UserVerification();
            newUser.setEmail(email);
            return newUser;
        });

        String otp = generateOtp();
        userVerification.setOtp(otp);
        userVerification.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepo.save(userVerification);

        sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        UserVerification userVerification = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userVerification.getOtpExpiry().isBefore(LocalDateTime.now())) return false;
        return otp.equals(userVerification.getOtp());
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    private void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + " (valid for 5 minutes)");
        mailSender.send(message);
    }
  
}
