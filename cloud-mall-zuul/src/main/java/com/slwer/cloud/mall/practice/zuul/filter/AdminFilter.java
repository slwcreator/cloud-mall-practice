package com.slwer.cloud.mall.practice.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.slwer.cloud.mall.practice.common.common.Constant;
import com.slwer.cloud.mall.practice.user.model.pojo.User;
import com.slwer.cloud.mall.practice.zuul.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 描述：管理员鉴权过滤器
 */
@Component
public class AdminFilter extends ZuulFilter {
    @Autowired
    UserFeignClient userFeignClient;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String requestURI = request.getRequestURI();
        if (requestURI.contains("adminLogin")) {
            return false;
        }
        return requestURI.contains("admin");
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            currentContext.setSendZuulResponse(false);
            currentContext.getResponse().setContentType("application/json;charset=utf-8");
            currentContext.setResponseBody("{\n" +
                    "    \"status\": 10010,\n" +
                    "    \"msg\": \"用户未登录\",\n" +
                    "    \"data\": null\n" +
                    "}");
            currentContext.setResponseStatusCode(200);
            return null;
        }

        //校验是否是管理员
        Boolean adminRole = userFeignClient.checkAdminRole(currentUser);
        if (!adminRole) {
            currentContext.setSendZuulResponse(false);
            currentContext.getResponse().setContentType("application/json;charset=utf-8");
            currentContext.setResponseBody("{\n" +
                    "    \"status\": 10011,\n" +
                    "    \"msg\": \"需要管理员权限\",\n" +
                    "    \"data\": null\n" +
                    "}");
            currentContext.setResponseStatusCode(200);
        }
        return null;
    }
}
