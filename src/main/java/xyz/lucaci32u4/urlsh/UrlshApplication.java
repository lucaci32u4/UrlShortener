package xyz.lucaci32u4.urlsh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties(Config.class)
public class UrlshApplication {

    public static void main(String[] args) {
        new SpringApplication(UrlshApplication.class)
                .run(args);
    }

}
