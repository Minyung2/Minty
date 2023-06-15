package com.Reboot.Minty.config;

import com.Reboot.Minty.interceptor.UserLocationInterceptor;
import com.Reboot.Minty.member.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserService userService;

    public WebMvcConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLocationInterceptor(userService))
                .addPathPatterns("/loginSuccess"); // 로그인 성공 후 리다이렉트되는 URL
    }


}
