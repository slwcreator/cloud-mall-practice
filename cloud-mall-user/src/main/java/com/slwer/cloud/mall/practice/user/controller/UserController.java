package com.slwer.cloud.mall.practice.user.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.slwer.cloud.mall.practice.common.common.ApiRestResponse;
import com.slwer.cloud.mall.practice.common.common.Constant;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import com.slwer.cloud.mall.practice.user.filter.UserFilter;
import com.slwer.cloud.mall.practice.user.model.pojo.User;
import com.slwer.cloud.mall.practice.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse<User> register(@RequestParam("userName") String userName,
                                          @RequestParam("password") String password) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    @GetMapping("/login")
    @ResponseBody
    public ApiRestResponse<User> login(@RequestParam("userName") String userName,
                                       @RequestParam("password") String password,
                                       HttpSession session) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(userName, password);
        user.setPassword(null);
        session.setAttribute(Constant.IMOOC_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse<User> updateUserInfo(HttpSession session,
                                                @RequestParam("signature") String signature) throws ImoocMallException {
//        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
//        if (currentUser == null) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
//        }
        User currentUser = UserFilter.userThreadLocal.get();
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse<User> logout(HttpSession session) {
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    @GetMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse<User> adminLogin(@RequestParam("userName") String userName,
                                            @RequestParam("password") String password,
                                            HttpSession session) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(userName, password);
        //校验是否是管理员
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }

    @PostMapping("/checkAdminRole")
    @ResponseBody
    public Boolean checkAdminRole(@RequestBody User user) {
        return userService.checkAdminRole(user);
    }

    @GetMapping("/getUser")
    @ResponseBody
    public User getUser(HttpSession session) {
        return (User) session.getAttribute(Constant.IMOOC_MALL_USER);
    }

    @GetMapping("/loginWithJwt")
    @ResponseBody
    public ApiRestResponse<String> loginWithJwt(@RequestParam("userName") String userName,
                                                @RequestParam("password") String password) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(userName, password);
        user.setPassword(null);
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        String token = JWT.create()
                .withClaim(Constant.USER_ID, user.getId())
                .withClaim(Constant.USER_NAME, user.getUsername())
                .withClaim(Constant.USER_ROLE, user.getRole())
                //过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + Constant.EXPIRE_TIME))
                .sign(algorithm);
        return ApiRestResponse.success(token);
    }
}