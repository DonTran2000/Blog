package com.don.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // Định nghĩa kiểu TEXT để lưu nội dung Markdown dài thoải mái
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String excerpt; // Đoạn trích ngắn hiển thị ở trang chủ

    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
