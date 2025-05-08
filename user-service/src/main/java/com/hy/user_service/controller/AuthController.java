package com.hy.user_service.controller;

import com.hy.security_common.provider.JwtTokenProvider;
import com.hy.user_service.dto.auth.LoginDto;
import com.hy.user_service.dto.auth.RegisterDto;
import com.hy.user_service.exception.UnauthorizedException;
import com.hy.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDto.RegisterResponse> register(@RequestBody RegisterDto.RegisterRequest request) {
        RegisterDto.RegisterResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto.LoginRequest request) {
        try {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        LoginDto.LoginResponse loginResponse =  userService.loadUserByUsername(request.getUsername());

        String token = jwtTokenProvider.generateToken(loginResponse.getId(), loginResponse.getUsername());
        return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
