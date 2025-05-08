package com.hy.user_service.controller;

import com.hy.security_common.provider.JwtTokenProvider;
import com.hy.user_service.dto.user.ProfileDto;
import com.hy.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDto.ProfileResponse> getProfile(@RequestHeader("Authorization") String token) {
        Long id = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileDto.ProfileRequest request) {
        Long id = jwtTokenProvider.getIdFromToken(token);
        userService.updateProfile(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> updateProfile(@RequestHeader("Authorization") String token) {
        Long id = jwtTokenProvider.getIdFromToken(token);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
