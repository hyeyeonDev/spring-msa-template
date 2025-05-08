package com.hy.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hy.user_service.dto.auth.LoginDto;
import com.hy.user_service.dto.auth.RegisterDto;
import com.hy.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 초기 데이터 설정 (필요 시)
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        RegisterDto.RegisterRequest registerRequest= new RegisterDto.RegisterRequest();
        // given
        userService.register(registerRequest);
        LoginDto.LoginRequest request = new LoginDto.LoginRequest();
        request.setUsername("john");
        request.setPassword("pass123");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_ShouldFail_WhenCredentialsAreInvalid() throws Exception {
        // given
        LoginDto.LoginRequest request = new LoginDto.LoginRequest();
        request.setUsername("john");
        request.setPassword("wrongPass");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    void register_ShouldSucceed_WhenValidRequest() throws Exception {
        // given
        LoginDto.LoginRequest request = new LoginDto.LoginRequest();
        request.setUsername("jane");
        request.setPassword("pass123");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
