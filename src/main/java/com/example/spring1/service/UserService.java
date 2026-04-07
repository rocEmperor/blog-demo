package com.example.spring1.service;

import com.example.spring1.entity.User;
import com.example.spring1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public User add(User user) {
        return userRepository.save(user);
    }
    public User getById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}
