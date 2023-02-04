package com.consiti.springsecurity1.security.service;

import com.consiti.springsecurity1.security.entity.User;
import com.consiti.springsecurity1.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    public Optional<User> getByUsername(String username){
        return  userRepository.findByUsername(username);
    }

    public boolean existByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void save(User user){
        userRepository.save(user);
    }
}
