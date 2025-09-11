package com.skrt.jwsindividuella.repositories;

import com.skrt.jwsindividuella.entities.BloggPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloggPostRepository extends JpaRepository<BloggPost, Long> {
}
