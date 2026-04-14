package com.example.blog.service;

import com.example.blog.common.BusinessException;
import com.example.blog.dto.*;
import com.example.blog.entity.PasswordResetCode;
import com.example.blog.entity.User;
import com.example.blog.repository.PasswordResetCodeRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetCodeRepository passwordResetCodeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String nickname = request.getNickname().trim();
        String email = request.getEmail().trim().toLowerCase();
        userRepository.findByUsernameAndDeletedAtIsNull(nickname).ifPresent(u -> { throw new BusinessException(409, "昵称已被占用"); });
        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) throw new BusinessException(409, "邮箱已被占用");
        User user = new User();
        user.setUsername(nickname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(new Date());
        user.setFailedLoginCount(0);
        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getId(), saved.getUsername());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(request.getNickname().trim())
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (user.getLockedUntil() != null && user.getLockedUntil().after(new Date())) {
            throw new BusinessException(423, "账号已锁定，请稍后再试");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int fails = user.getFailedLoginCount() + 1;
            user.setFailedLoginCount(fails);
            if (fails >= 10) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, 30);
                user.setLockedUntil(c.getTime());
            }
            userRepository.save(user);
            throw new BusinessException(401, "密码错误");
        }
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, "Bearer", user.getId(), user.getUsername(), user.getEmail());
    }

    @Transactional
    public ForgotPasswordSendResponse sendForgotCode(ForgotPasswordSendRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new BusinessException(404, "该邮箱未注册"));
        passwordResetCodeRepository.findTopByEmailOrderByCreatedAtDesc(email).ifPresent(last -> {
            long elapsed = (System.currentTimeMillis() - last.getCreatedAt().getTime()) / 1000;
            if (elapsed < 60) throw new BusinessException(429, "发送过于频繁，请稍后再试");
        });
        List<PasswordResetCode> active = passwordResetCodeRepository.findByEmailAndConsumedFalse(email);
        for (PasswordResetCode c : active) c.setConsumed(true);
        passwordResetCodeRepository.saveAll(active);
        String code = String.format("%06d", new Random().nextInt(1000000));
        PasswordResetCode row = new PasswordResetCode();
        row.setEmail(email);
        row.setCode(code);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        row.setExpiresAt(cal.getTime());
        row.setConsumed(false);
        row.setCreatedAt(new Date());
        passwordResetCodeRepository.save(row);
        return new ForgotPasswordSendResponse(60);
    }

    @Transactional
    public void resetPassword(ForgotPasswordResetRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new BusinessException(404, "该邮箱未注册"));
        PasswordResetCode row = passwordResetCodeRepository.findTopByEmailAndConsumedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BusinessException(400, "验证码无效或已过期"));
        if (row.getExpiresAt().before(new Date())) throw new BusinessException(400, "验证码已过期");
        if (!row.getCode().equals(request.getCode().trim())) throw new BusinessException(400, "验证码错误");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        row.setConsumed(true);
        passwordResetCodeRepository.save(row);
    }
}
