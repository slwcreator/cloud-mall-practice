package com.slwer.cloud.mall.practice.cartorder.filter;

import com.slwer.cloud.mall.practice.cartorder.model.pojo.User;
import com.slwer.cloud.mall.practice.common.common.Constant;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 用户校验过滤器
 */
public class UserFilter implements Filter {

    public static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();
    public User currentUser = new User();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //通过 RequestContextHolder 获取本地请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }

        //获取本地线程绑定的请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        //给请求模板附加本地线程头部信息，把 User 信息放到 ThreadLocal 里
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    Enumeration<String> values = request.getHeaders(headerName);
                    if (values != null) {
                        while (values.hasMoreElements()) {
                            String value = values.nextElement();
                            if (headerName.equals(Constant.USER_ID)) {
                                currentUser.setId(Integer.valueOf(value));
                            }
                            if (headerName.equals(Constant.USER_NAME)) {
                                currentUser.setUsername(value);
                            }
                            if (headerName.equals(Constant.USER_ROLE)) {
                                currentUser.setRole(Integer.valueOf(value));
                            }
                            if (currentUser.getId() != null && currentUser.getUsername() != null && currentUser.getRole() != null) {
                                userThreadLocal.set(currentUser);
                            }
                        }
                    }
                }

            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
