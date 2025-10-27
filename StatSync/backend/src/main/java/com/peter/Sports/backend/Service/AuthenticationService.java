package com.peter.Sports.backend.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peter.Sports.backend.DTO.AuthRequest;
import com.peter.Sports.backend.DTO.AuthResponse;
import com.peter.Sports.backend.DTO.ForgotPasswordsConfirmation;
import com.peter.Sports.backend.DTO.RefreshTokenRequest;
import com.peter.Sports.backend.DTO.VerifyForgotPassword;
import com.peter.Sports.backend.Model.Users;
import com.peter.Sports.backend.Repository.UsersRepository;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, 
    UsersRepository usersRepository, EmailService emailService, PasswordEncoder passwordEncoder){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticatedAndGetToken(@Valid AuthRequest authRequest){
        //technically what the user entered in for email or name
        String identifier = authRequest.getIdentifier();
        String password = authRequest.getPassword();

        //checking if it is email
        Boolean isEmail = identifier.contains("@");
        Users user; 
        if(isEmail){
            user = usersRepository.findByEmail(identifier)
                    .orElseThrow(()  -> new RuntimeException("User not found"));
        }else{            
            user = usersRepository.findByName(identifier)
                    .orElseThrow(()  -> new RuntimeException("User not found"));
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getName(), password)
        );
        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateToken(user.getName());
            String refreshToken = jwtService.generateRefreshToken(user.getName());

            user.setRefreshToken(refreshToken);
            usersRepository.save(user);

            return new AuthResponse(accessToken, refreshToken);
        } else {
            throw new UsernameNotFoundException("Invalid User Request!");
        }
    }

    public AuthResponse refresh(RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();
        if (jwtService.isTokenExpired(refreshToken)){
            throw new RuntimeErrorException(null,"Refresh Token is Expired");
        }
        String name = jwtService.extractName(refreshToken);
        String newAccessToken = jwtService.generateToken(name);
        String newRefreshToken = jwtService.generateRefreshToken(name);

        Users user = usersRepository.findByName(name)
        .orElseThrow(() -> new RuntimeException("User not found")); 
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }        
        user.setRefreshToken(newRefreshToken);
        usersRepository.save(user);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public ResponseEntity<?> logout(String token){
        String identifier = jwtService.extractName(token);
        Users user;
        if(identifier.contains("@")){
            user = usersRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = usersRepository.findByName(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        
        user.setRefreshToken(null);
        usersRepository.save(user);

        return ResponseEntity.ok("Logged out");
    
    }

    public void sendForgotPasswordRecovery(String email){
        Optional<Users> optionalUser = usersRepository.findByEmailIgnoreCase(email);
        if(optionalUser.isPresent()){
            Users user = optionalUser.get();
            LocalDateTime userExpiredDateTime = user.getVerificationCodeExpiredTime();
            if(userExpiredDateTime == null || userExpiredDateTime.isBefore(LocalDateTime.now())){
                user.setVerificationCode(generateVerificationCode());
                user.setVerificationCodeExpiredTime(LocalDateTime.now().plusMinutes(15));
                sendVerificationEmail(user);
                usersRepository.save(user);
            } else {
                throw new RuntimeException("Email has already been sent");
            }
        } else {
            throw new RuntimeException("User not found");
        }

    }

    public void sendVerificationEmail(Users user){
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Forgot Password Recovery</h2>" 
                + "<h3 style=\"color: gray\"> Code Expires In 15 minutes</h3>"
                + "<p style=\"font-size: 16px;\">Please Enter the Verification Code Below to Continue</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public Boolean verifyVerificationCode(VerifyForgotPassword input){
        Optional<Users> optionalUser = usersRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            Users user = optionalUser.get();
            LocalDateTime userExpiredDateTime = user.getVerificationCodeExpiredTime();
            String userVerificationCode = user.getVerificationCode();
            if(input.getVerificationCode().equals(userVerificationCode) && 
                LocalDateTime.now().isBefore(userExpiredDateTime)){
                    user.setVerificationCode(null);
                    user.setVerificationCodeExpiredTime(null);
                    usersRepository.save(user);
                    return true;
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        }
        return false;
    }

    public Boolean createNewPassword(ForgotPasswordsConfirmation input){
        Optional<Users> userOptional = usersRepository.findByEmail(input.getEmail());
        if(userOptional.isPresent()){
            Users user = userOptional.get();
            if (!input.getNewPassword().equals(input.getConfirmPassword())) {
                throw new RuntimeException("Passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(input.getNewPassword()));
            usersRepository.save(user);
            return true;            
        }
        return false;
    }

    public Users getCurrentUser(UsersRepository usersRepository)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return usersRepository.findByName(name).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
