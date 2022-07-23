package xyz.lucaci32u4.urlsh;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "brand")
@Validated
public record Config (
        @NotBlank String domain,
        @NotBlank String name
) {

}
