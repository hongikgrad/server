package com.hongikgrad;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.hongikgrad")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedOrigins("https://localhost:8080")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://localhost:3000")
                .allowedOrigins("http://hongik-grad.cf")
                .allowedOrigins("https://hongik-grad.cf")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
