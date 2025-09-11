package com.skrt.jwsindividuella.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity @Table(name = "blogg_posts")
public class BloggPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private String ownerSub;

    @Column(nullable = false,updatable = false)
    private String ownerIdentifier;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        createdDate = updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getOwnerSub() {
        return ownerSub;
    }
    public void setOwnerSub(String ownerSub) {
        this.ownerSub = ownerSub;
    }
    public String getOwnerIdentifier() {
        return ownerIdentifier;
    }
    public void setOwnerIdentifier(String ownerEmail) {
        this.ownerIdentifier = ownerEmail;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

}
