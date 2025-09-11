package com.skrt.jwsindividuella.services;

import com.skrt.jwsindividuella.dtos.BloggPostDTO;
import com.skrt.jwsindividuella.entities.BloggPost;
import com.skrt.jwsindividuella.exceptions.ForbiddenOperationException;
import com.skrt.jwsindividuella.exceptions.NotFoundException;
import com.skrt.jwsindividuella.repositories.BloggPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BloggPostService {

    private static final Logger logger = LoggerFactory.getLogger(BloggPostService.class);
    private final BloggPostRepository bloggPostRepository;

    @Autowired
    public BloggPostService(BloggPostRepository blogPostRepository) {
        this.bloggPostRepository = blogPostRepository;
    }

    @Transactional(readOnly = true)
    public List<BloggPost> allPosts() {
        return bloggPostRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BloggPost findById(Long id) {
        return bloggPostRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("BloggPost", "id", id));
    }

    @Transactional
    public BloggPost create(BloggPostDTO.CreateRequest dto, Authentication auth){
        Jwt jwt = (Jwt) auth.getPrincipal();
        String sub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        String ownerIdentifier = (email != null && !email.isBlank()) ? email : preferredUsername;

        logger.debug("Creating bloggpost by sub={}, email/preferredUsername={}", sub, ownerIdentifier);
        System.out.println("Creating bloggpost by sub={" + sub + "}");

        BloggPost post = new BloggPost();
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setOwnerSub(sub);
        post.setOwnerIdentifier(ownerIdentifier != null ? ownerIdentifier : sub);
        return bloggPostRepository.save(post);

    }

    @Transactional
    public BloggPost update(BloggPostDTO.UpdateRequest dto, Authentication auth){
        BloggPost existing = findById(dto.id());
        validateOwner(existing, auth);
        existing.setTitle(dto.title());
        existing.setContent(dto.content());
        return bloggPostRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Authentication auth, boolean isAdmin){
        BloggPost existing = findById(id);
        if(!isAdmin){
            validateOwner(existing, auth);
        }
        bloggPostRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public long count() {
        return bloggPostRepository.count();
    }

    private void validateOwner(BloggPost post, Authentication auth) {
        String sub = ((Jwt) auth.getPrincipal()).getClaimAsString("sub");
        if(!sub.equals(post.getOwnerSub())){
            throw new ForbiddenOperationException(sub, "BloggPost", post.getId());
        }
    }


}
