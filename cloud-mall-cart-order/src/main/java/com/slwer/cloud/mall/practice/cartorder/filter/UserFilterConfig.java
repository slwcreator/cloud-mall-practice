package com.slwer.cloud.mall.practice.cartorder.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserFilterConfig {

    @Bean
    public UserFilter userFilter() {
        return new UserFilter();
    }

    @Bean(name = "userFilterConf")
    public FilterRegistrationBean<UserFilter> userFilterConf() {
        FilterRegistrationBean<UserFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(userFilter());
        filterRegistrationBean.addUrlPatterns("/cart/*");
        filterRegistrationBean.addUrlPatterns("/order/*");
        filterRegistrationBean.addUrlPatterns("/user/update");
        filterRegistrationBean.setName("userFilterConf");
        return filterRegistrationBean;
    }
}
