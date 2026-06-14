package com.don.blog.controller;

import com.don.blog.domain.Post;
import com.don.blog.form.LoginRequest;
import com.don.blog.form.PostForm;
import com.don.blog.repository.PostRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Cấp quyền cho React FE gọi API
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Value("${blog.admin.secret-key}")
    private String adminPassword;

    private static String activeAdminToken = null;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getPassword() != null && request.getPassword().equals(adminPassword)) {
            // Tạo ra một Token ngẫu nhiên hoàn toàn mới không trùng lặp
            activeAdminToken = UUID.randomUUID().toString();

            // Trả Token về cho Frontend lưu tạm
            Map<String, String> response = new HashMap<>();
            response.put("token", activeAdminToken);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu Admin không chính xác!");
    }

    // 1. Mọi người xem bài viết (Public-read)
    @GetMapping("/posts")
    public List<Post> getAllPosts(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty() && !category.equals("Tất cả")) {
            return postRepository.findByCategoryOrderByCreatedAtDesc(category);
        }
        // Ngược lại nếu không truyền hoặc chọn "Tất cả", lấy toàn bộ bài viết
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // Xem chi tiết 1 bài viết
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @RequestBody Post post) {

        // Định dạng Header gửi lên thường là: "Bearer <token>"
        String token = (bearerToken != null && bearerToken.startsWith("Bearer "))
                ? bearerToken.substring(7) : null;

        // Kiểm tra xem Token gửi lên có trùng khớp với Token đang hoạt động trên Server không
        if (token == null || activeAdminToken == null || !token.equals(activeAdminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Lỗi bảo mật: Token đã hết hạn hoặc bạn không có quyền Admin!");
        }

        Post savedPost = postRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }
}
