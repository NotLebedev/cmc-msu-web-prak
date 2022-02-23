package ru.notlebedev.webprak.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/health.html").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/reports/**").addResourceLocations("file:/home/artemiy/Documents/cmc-msu-web-prak/build/reports/");
    }
}
