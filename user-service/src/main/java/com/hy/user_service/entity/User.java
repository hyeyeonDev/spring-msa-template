package com.hy.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId"}),
        indexes = {
                @Index(name = "idx_is_active", columnList = "isActive")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    private String role;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private boolean isActive;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(); // 현재 시간으로 설정
        this.isActive = true; // 기본값으로 true 설정
    }
}
