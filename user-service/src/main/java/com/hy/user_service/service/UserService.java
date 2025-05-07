package com.hy.user_service.service;

import com.hy.security_common.provider.JwtTokenProvider;
import com.hy.user_service.dto.AuthDto;
import com.hy.user_service.entity.User;
import com.hy.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDto.RegisterResponse register(AuthDto.RegisterRequest request) {
        // 중복 사용자 이름 체크
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

//        // 테넌트 ID 유효성 검사
//        if (!multiTenancyProperties.getTenants().containsKey(request.getTenantId())) {
//            throw new IllegalArgumentException("Invalid tenant ID: " + request.getTenantId());
//        }

        // 사용자 정보 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        // JWT 토큰 생성 (선택적)
        String token = jwtTokenProvider.generateToken(request.getUsername());

        // 응답 생성
        AuthDto.RegisterResponse response = new AuthDto.RegisterResponse();
        response.setUsername(request.getUsername());
        response.setTenantId(request.getTenantId());
        response.setRole(request.getRole());
        response.setToken(token);
        return response;
    }

}
