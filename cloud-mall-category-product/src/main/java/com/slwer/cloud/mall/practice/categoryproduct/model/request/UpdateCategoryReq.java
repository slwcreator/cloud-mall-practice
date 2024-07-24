package com.slwer.cloud.mall.practice.categoryproduct.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 描述：【更新目录分类】接口的参数类
 */
public class UpdateCategoryReq {

    @NotNull(message = "目录id不能为null")
    private Integer id;
    @Size(min = 2, max = 5, message = "分类目录名称长度必须在2到5之间")
    private String name;

    @Max(value = 3, message = "分类目录最多为3级")
    private Integer type;

    private Integer parentId;

    private Integer orderNum;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UpdateCategoryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                ", updateTime=" + updateTime +
                '}';
    }
}
