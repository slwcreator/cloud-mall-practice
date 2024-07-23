package com.slwer.cloud.mall.practice.common.exception;

/**
 * 异常枚举
 */
public enum ImoocMallExceptionEnum {

    REQUEST_PARAM_ERROR(10001, "参数错误，请重试"),
    NO_ENUM(10002, "未找到对应的枚举"),
    NEED_USER_NAME(10003, "用户名不能为空"),
    NEED_PASSWORD(10004, "密码不能为空"),
    PASSWORD_TOO_SHORT(10005, "密码长度不能小于8位"),
    NAME_EXISTED(10006, "不允许重名"),
    INSERT_FAILED(10007, "用户插入失败，请重试"),
    WRONG_PASSWORD(10008, "用户名或密码错误"),
    UPDATE_FAILED(10009, "更新失败"),
    NEED_LOGIN(10010, "用户未登录"),
    NEED_ADMIN(10011, "无管理员权限"),
    SYSTEM_ERROR(20000, "系统异常");

    //异常码
    final Integer code;

    //异常信息
    final String msg;

    ImoocMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
