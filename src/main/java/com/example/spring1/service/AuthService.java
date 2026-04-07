package com.example.spring1.service;

import com.example.spring1.common.BusinessException;
import com.example.spring1.dto.AuthResponse;
import com.example.spring1.dto.LoginRequest;
import com.example.spring1.entity.User;
import com.example.spring1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public AuthResponse register(String username, String rawPassword) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new BusinessException(400, "用户名已存在");
        });
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCreateTime(new Date());
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId(), saved.getUsername());
        return new AuthResponse(token, "Bearer", saved.getId(), saved.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, "Bearer", user.getId(), user.getUsername());
    }
}
