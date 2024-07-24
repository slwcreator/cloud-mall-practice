package com.slwer.cloud.mall.practice.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Category;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.UpdateCategoryReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(UpdateCategoryReq updateCategoryReq);

    void delete(Integer id);

    PageInfo<Category> listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listForCustomer(Integer parentId);
}
