package com.skrt.jwsindividuella.controllers;

import com.skrt.jwsindividuella.dtos.BloggPostDTO;
import com.skrt.jwsindividuella.entities.BloggPost;
import com.skrt.jwsindividuella.services.BloggPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class BloggPostController {

    private final BloggPostService service;

    @Autowired
    public BloggPostController(BloggPostService service) {
        this.service = service;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<BloggPost>> getAllPosts() {
        return ResponseEntity.ok(service.allPosts());
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<BloggPost> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/newpost")
    public ResponseEntity<BloggPost> createPost(@Valid @RequestBody BloggPostDTO.CreateRequest dto, Authentication auth) {
        BloggPost created = service.create(dto, auth);

        return ResponseEntity
                .created(URI.create("/api/v2/post/" + created.getId()))
                .body(created);
    }

    @PutMapping("/updatepost")
    public ResponseEntity<BloggPost> updatePost(@Valid @RequestBody BloggPostDTO.UpdateRequest dto, Authentication auth) {
        BloggPost updated = service.update(dto, auth);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/deletepost/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        service.delete(id,auth,isAdmin);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }


}
