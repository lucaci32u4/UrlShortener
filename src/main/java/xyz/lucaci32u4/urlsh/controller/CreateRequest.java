package xyz.lucaci32u4.urlsh.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record CreateRequest(
        @NotBlank String url,
        @Pattern(regexp = "[0-9a-z_-]{2,30}") String alias,
        @NotNull Boolean tracking,
        @NotNull Boolean inpageRedirect

) {
}
