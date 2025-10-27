package com.peter.Sports.backend.Service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peter.Sports.backend.Model.Users;
import com.peter.Sports.backend.Repository.UsersRepository;

@Service
public class MyUserDetailService implements UserDetailsService{

    private final UsersRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    @Autowired
    public MyUserDetailService(UsersRepository usersRepository, PasswordEncoder passwordEncoder){
        this.userRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByName(username);
        if(user.isPresent()){
            var userObj = user.get();
            return User.builder()
                .username(userObj.getName())
                .password(userObj.getPassword())
                .roles(getRoles(userObj))
                .build();
        } else{
            throw new UsernameNotFoundException(username);
        }
    } 

    private String getRoles(Users user){
        if(user.getRole() == null){
            return "";
        }
        return user.getRole();
    }

    public String addUser(Users user){
        Optional<Users> userOptional = userRepository.findByName(user.getName());
        Optional<Users> userOptionalByEmail = userRepository.findByEmail(user.getEmail());
        
        if(userOptional.isPresent()){
            return "Username is Taken";
        }
        else if(userOptionalByEmail.isPresent()){
            return "Email is Taken";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFavoriteNBA(new ArrayList<>());
        user.setFavoriteNFL(new ArrayList<>());
        user.setRole("USER");
        userRepository.save(user);
        return "User added successfully";
    }



}
