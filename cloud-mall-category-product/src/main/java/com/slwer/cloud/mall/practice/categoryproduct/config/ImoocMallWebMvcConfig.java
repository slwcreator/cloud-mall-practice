package com.slwer.cloud.mall.practice.categoryproduct.config;

import com.slwer.cloud.mall.practice.categoryproduct.common.ProductConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置地址映射
 */
@Configuration
public class ImoocMallWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        //商品图片等定义静态资源映射
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + ProductConstant.FILE_UPLOAD_DIR);
    }
}
