package com.hy.user_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LoginDto {

    @Getter
    @Setter
    public static  class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

    }

    @Getter
    @Setter
    @Builder
    public static class LoginResponse {
        private Long id;
        private String username;
    }
}
