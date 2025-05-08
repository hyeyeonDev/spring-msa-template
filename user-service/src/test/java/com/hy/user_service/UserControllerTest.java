package com.hy.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hy.security_common.provider.JwtTokenProvider;
import com.hy.user_service.dto.auth.LoginDto;
import com.hy.user_service.dto.user.ProfileDto;
import com.hy.user_service.entity.User;
import com.hy.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = new User();
        user.setUsername("john");
        user.setPassword("encodedPass");
        user.setEmail("john@example.com");
        user = userRepository.save(user);

        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User("john", "", new ArrayList<>()), null);
        // 유효한 토큰 생성
        validToken = "Bearer " + jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User("wrongUser", "", new ArrayList<>()), null);
        // 잘못된 사용자 ID로 유효하지 않은 토큰 생성
        invalidToken = "Bearer " + jwtTokenProvider.generateToken(10L, "wrongUser");
    }

    @Test
    void getProfile_ShouldReturnProfile_WhenTokenIsValid() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{id}/profile", user.getId())
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getProfile_ShouldFail_WhenTokenIsInvalid() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{id}/profile", user.getId())
                        .header("Authorization", invalidToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Unauthorized access to profile"));
    }

    @Test
    void getProfile_ShouldFail_WhenUserNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/999/profile")
                        .header("Authorization", validToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 999"));
    }

    @Test
    void updateProfile_ShouldUpdateEmail_WhenTokenIsValid() throws Exception {
        // given
        ProfileDto.ProfileRequest request = new ProfileDto.ProfileRequest();
        request.setEmail("newemail@example.com");

        // when & then
        mockMvc.perform(put("/api/users/{id}/profile", user.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assert updatedUser.getEmail().equals("newemail@example.com");
    }

    @Test
    void updateProfile_ShouldFail_WhenTokenIsInvalid() throws Exception {
        // given
        ProfileDto.ProfileRequest request = new ProfileDto.ProfileRequest();
        request.setEmail("newemail@example.com");

        // when & then
        mockMvc.perform(put("/api/users/{id}/profile", user.getId())
                        .header("Authorization", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Unauthorized access to update profile"));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenTokenIsValid() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/{id}/delete", user.getId())
                        .header("Authorization", validToken))
                .andExpect(status().isNoContent());

        assert !userRepository.findById(user.getId()).isPresent();
    }

    @Test
    void deleteUser_ShouldFail_WhenTokenIsInvalid() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/{id}/delete", user.getId())
                        .header("Authorization", invalidToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Unauthorized access to delete user"));
    }

    @Test
    void deleteUser_ShouldFail_WhenUserNotFound() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/999/delete")
                        .header("Authorization", validToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 999"));
    }
}
