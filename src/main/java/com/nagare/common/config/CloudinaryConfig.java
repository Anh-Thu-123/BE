package com.nagare.common.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${app.cloudinary-url:}") String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank()) {
            return new Cloudinary(); // cau hinh trong rong cho moi truong dev/test chua co Cloudinary that
        }
        return new Cloudinary(cloudinaryUrl);
    }
}
