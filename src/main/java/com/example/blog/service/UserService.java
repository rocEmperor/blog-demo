package com.example.blog.service;

import com.example.blog.common.BusinessException;
import com.example.blog.dto.DeleteAccountRequest;
import com.example.blog.dto.UpdateProfileRequest;
import com.example.blog.dto.UpdateSecurityRequest;
import com.example.blog.dto.UserMeDto;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public User requireById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    public User requireActiveById(Integer id) {
        User user = requireById(id);
        if (user.getDeletedAt() != null) throw new BusinessException(404, "用户不存在");
        return user;
    }

    public UserMeDto toMeDto(User user) {
        UserMeDto dto = new UserMeDto();
        dto.setUserId(user.getId());
        dto.setNickname(user.getUsername());
        dto.setEmail(user.getEmail() != null ? user.getEmail() : "");
        dto.setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        dto.setBio(user.getBio() != null ? user.getBio() : "");
        dto.setPhone(user.getPhone() != null ? user.getPhone() : "");
        return dto;
    }

    @Transactional
    public UserMeDto updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = requireActiveById(userId);
        String nickname = request.getNickname().trim();
        if (!nickname.equals(user.getUsername())) {
            userRepository.findByUsernameAndDeletedAtIsNull(nickname).ifPresent(other -> {
                if (!other.getId().equals(userId)) throw new BusinessException(409, "昵称已被占用");
            });
            user.setUsername(nickname);
        }
        user.setBio(request.getBio().trim());
        String phone = request.getPhone() != null ? request.getPhone().trim() : "";
        if (phone.isEmpty()) user.setPhone(null);
        else {
            if (userRepository.existsByPhoneAndDeletedAtIsNullAndIdNot(phone, userId)) {
                throw new BusinessException(409, "该手机号已被占用");
            }
            user.setPhone(phone);
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }
        userRepository.save(user);
        return toMeDto(user);
    }

    @Transactional
    public void updateSecurity(Integer userId, UpdateSecurityRequest request) {
        User user = requireActiveById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(400, "当前密码错误");
        }
        String email = request.getEmail().trim().toLowerCase();
        userRepository.findByEmailAndDeletedAtIsNull(email).ifPresent(other -> {
            if (!other.getId().equals(userId)) throw new BusinessException(409, "邮箱已被占用");
        });
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void softDeleteAccount(Integer userId, DeleteAccountRequest request) {
        User user = requireActiveById(userId);
        if (request != null && request.getConfirmEmail() != null) {
            String confirm = request.getConfirmEmail().trim();
            if (!confirm.isEmpty()) {
                String current = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
                if (!confirm.toLowerCase().equals(current)) throw new BusinessException(400, "确认邮箱与当前账号邮箱不一致");
            }
        }
        user.setDeletedAt(new Date());
        userRepository.save(user);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}
