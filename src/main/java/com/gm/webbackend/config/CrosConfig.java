package com.gm.webbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public abstract class CrosConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    //是否发送Cookie
                    .allowCredentials(true)
                    //放行哪些原始域
                    .allowedOrigins("*")
//                    .allowedOriginPatterns("*")
                    .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE"})
                    .allowedHeaders("*")
                    .exposedHeaders("*");
        }

}
