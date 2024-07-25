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
    CREATE_FAILED(10012, "新增失败"),
    DELETE_FAILED(10013, "删除失败"),
    MKDIR_FAILED(10014, "文件夹创建失败"),
    UPLOAD_FAILED(10015, "文件上传失败"),
    NOT_SALE(10016, "商品状态不可售"),
    NOT_ENOUGH(10017, "商品库存不足"),
    CART_EMPTY(10018, "购物车已勾选的商品为空"),
    NO_ORDER(10019, "未找到对应的枚举"),
    NOT_YOUR_ORDER(10020, "订单不属于你"),
    CANCEL_WRONG_ORDER_STATUS(10033, "订单状态有误，付款后不支持取消订单"),
    PAY_WRONG_ORDER_STATUS(10034, "订单状态有误，仅能在未付款时付款"),
    DELIVER_WRONG_ORDER_STATUS(10035, "订单状态有误，仅能在付款后发货"),
    FINISH_WRONG_ORDER_STATUS(10036, "订单状态有误，仅能在发货后完结订单"),
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
