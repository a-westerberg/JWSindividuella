package com.skrt.jwsindividuella.dtos;

import jakarta.validation.constraints.*;

public class BloggPostDTO {

    public record CreatRequest(@NotBlank String title, @NotBlank String content) {

    }

    public record UpdateRequest(@NotBlank Long id, @NotBlank String title, @NotBlank String content) {

    }

}
