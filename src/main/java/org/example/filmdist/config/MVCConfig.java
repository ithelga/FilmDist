package org.example.filmdist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * class to set up work of Spring MVC
 */
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * to define directory with uploads-file
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file://" + uploadPath + "/");
    }

    /**
     * to override Spring Security default login-page
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
}