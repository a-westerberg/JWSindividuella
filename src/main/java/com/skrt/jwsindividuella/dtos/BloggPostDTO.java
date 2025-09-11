package com.skrt.jwsindividuella.dtos;

import jakarta.validation.constraints.*;

public class BloggPostDTO {

    public record CreateRequest(@NotBlank String title, @NotBlank String content) {

    }

    public record UpdateRequest(@NotNull Long id, @NotBlank String title, @NotBlank String content) {

    }

}
