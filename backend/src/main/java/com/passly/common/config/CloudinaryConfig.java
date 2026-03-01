package com.passly.common.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final AppProperties appProperties;

    @Bean
    public Cloudinary cloudinary() {
        AppProperties.Cloudinary c = appProperties.getCloudinary();
        return new Cloudinary(Map.of(
                "cloud_name", c.getCloudName(),
                "api_key",    c.getApiKey(),
                "api_secret", c.getApiSecret(),
                "secure",     true
        ));
    }
}
