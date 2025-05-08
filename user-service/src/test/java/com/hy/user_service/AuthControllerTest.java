package com.hy.user_service;

import com.hy.multi_tenant_core.properties.MultiTenancyProperties;
import com.hy.user_service.dto.AuthDto;
import com.hy.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MultiTenancyProperties multiTenancyProperties;

    @BeforeEach
    void setUp() {
        // 테스트용 테넌트 설정
        MultiTenancyProperties.DataSourceProperties dsProps = new MultiTenancyProperties.DataSourceProperties();
        dsProps.setUrl("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dsProps.setUsername("sa");
        dsProps.setPassword("");
        dsProps.setDriverClassName("org.h2.Driver");

        multiTenancyProperties.setTenants(new HashMap<>());
        multiTenancyProperties.getTenants().put("tenant1", dsProps);

        // 기존 사용자 데이터 삭제
        userRepository.deleteAll();
    }

    @Test
    void testSuccessfulRegistration() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setTenantId("tenant1");
        request.setRole("USER");

        ResponseEntity<AuthDto.RegisterResponse> response = restTemplate.postForEntity(
                "/api/v1/auth/register", request, AuthDto.RegisterResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("tenant1", response.getBody().getTenantId());
        assertEquals("USER", response.getBody().getRole());
        assertNotNull(response.getBody().getToken());

        // 데이터베이스에 사용자 저장 확인
        assertTrue(userRepository.findByUsername("testuser").isPresent());
    }

    @Test
    void testDuplicateUsername() {
        // 첫 번째 사용자 등록
        AuthDto.RegisterRequest request1 = new AuthDto.RegisterRequest();
        request1.setUsername("testuser");
        request1.setPassword("password123");
        request1.setTenantId("tenant1");
        request1.setRole("USER");
        restTemplate.postForEntity("/api/v1/auth/register", request1, AuthDto.RegisterResponse.class);

        // 동일한 사용자 이름으로 재시도
        AuthDto.RegisterRequest request2 = new AuthDto.RegisterRequest();
        request2.setUsername("testuser");
        request2.setPassword("password456");
        request2.setTenantId("tenant1");
        request2.setRole("USER");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/auth/register", request2, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Username already exists"));
    }

    @Test
    void testInvalidTenantId() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setTenantId("invalid_tenant");
        request.setRole("USER");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/auth/register", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid tenant ID"));
    }

    @Test
    void testInvalidInput() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setUsername(""); // 빈 사용자 이름
        request.setPassword("pass"); // 너무 짧은 비밀번호
        request.setTenantId("tenant1");
        request.setRole("USER");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/auth/register", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Validation 에러 메시지 확인 가능
    }
}
