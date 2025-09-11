package com.skrt.jwsindividuella.services;

import com.skrt.jwsindividuella.dtos.BloggPostDTO;
import com.skrt.jwsindividuella.entities.BloggPost;
import com.skrt.jwsindividuella.repositories.BloggPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloggPostService {

    private static final Logger logger = LoggerFactory.getLogger(BloggPostService.class);
    private final BloggPostRepository bloggPostRepository;

    @Autowired
    public BloggPostService(BloggPostRepository blogPostRepository) {
        this.bloggPostRepository = blogPostRepository;
    }

    public List<BloggPost> allPosts() {
        return bloggPostRepository.findAll();
    }
        // TODO Exception har en placeholder atm
    public BloggPost findById(Long id) {
        return bloggPostRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("BloggPost with id: " + id + " not found!"));
    }

    public BloggPost create(BloggPostDTO.CreatRequest dto, Authentication auth){
        Jwt jwt = (Jwt) auth.getPrincipal();
        String sub = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String preferredUserName = jwt.getClaimAsString("preferred_username");
        String ownerIdentifier = (email != null && !email.isBlank()) ? email : preferredUserName;

        logger.debug("Creating bloggpost by sub={}, email/preferredUserName={}", sub, ownerIdentifier);
        System.out.println("Creating bloggpost by sub={" + sub + "}");

        BloggPost post = new BloggPost();
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setOwnerSub(sub);
        post.setOwnerIdentifier(ownerIdentifier != null ? ownerIdentifier : sub);
        return bloggPostRepository.save(post);

    }

    public BloggPost update(BloggPostDTO.UpdateRequest dto, Authentication auth){
        BloggPost existing = findById(dto.id());
        ensureOwner(existing, auth);
        existing.setTitle(dto.title());
        existing.setContent(dto.content());
        return bloggPostRepository.save(existing);
    }


    //TODO lägg till exeption och hantera om en användare som inte är admin eller owner försöker ta bort??
    public void delete(Long id, Authentication auth, boolean isAdmin){
        BloggPost existing = findById(id);
        if(!isAdmin){
            ensureOwner(existing, auth);
            bloggPostRepository.delete(existing);
        }
    }

    public long count() {
        return bloggPostRepository.count();
    }

    private void ensureOwner(BloggPost post, Authentication auth) {
        String sub = ((Jwt) auth.getPrincipal()).getClaimAsString("sub");
        if(!sub.equals(post.getOwnerSub())){
            throw new SecurityException("User does not own this post");
        }
    }


}
