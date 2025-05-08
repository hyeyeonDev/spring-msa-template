package com.hy.user_service.service;

import com.hy.security_common.provider.JwtTokenProvider;
import com.hy.user_service.dto.auth.LoginDto;
import com.hy.user_service.dto.auth.RegisterDto;
import com.hy.user_service.dto.user.ProfileDto;
import com.hy.user_service.entity.User;
import com.hy.user_service.exception.NotFoundException;
import com.hy.user_service.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginDto.LoginResponse loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return LoginDto.LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public RegisterDto.RegisterResponse register(RegisterDto.RegisterRequest request) {
        // 중복 사용자 이름 체크
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // 사용자 정보 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        // 응답 생성
        RegisterDto.RegisterResponse response = new RegisterDto.RegisterResponse();
        response.setUsername(request.getUsername());
        response.setRole(request.getRole());
        return response;
    }

    public ProfileDto.ProfileResponse getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ProfileDto.ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public void updateProfile(Long id, ProfileDto.ProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(request.getEmail());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

}
