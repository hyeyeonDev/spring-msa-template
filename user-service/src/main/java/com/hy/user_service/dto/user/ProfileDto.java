package com.hy.user_service.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ProfileDto {

    @Getter
    @Setter
    public static class ProfileRequest {
        private Long id;
        private String email;
    }

    @Getter
    @Setter
    @Builder
    public static class ProfileResponse {
        private Long id;
        private String username;
        private String email;
    }
}
