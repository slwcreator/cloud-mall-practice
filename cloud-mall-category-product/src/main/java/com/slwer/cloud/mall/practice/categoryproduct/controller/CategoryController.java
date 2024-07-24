package com.slwer.cloud.mall.practice.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Category;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.UpdateCategoryReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.slwer.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.slwer.cloud.mall.practice.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 目录控制器
 */
@Controller
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse<Category> addCategory(HttpSession session,
                                                 @Valid @RequestBody AddCategoryReq addCategoryReq) {
        categoryService.add(addCategoryReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台更新商品分类目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse<Category> updateCategory(HttpSession session,
                                                    @Valid @RequestBody UpdateCategoryReq updateCategoryReq) {
        categoryService.update(updateCategoryReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品分类目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse<Category> deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台分类目录列表")
    @GetMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse<PageInfo<Category>> listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<Category> pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台分类目录列表")
    @GetMapping("/category/list")
    @ResponseBody
    public ApiRestResponse<List<CategoryVO>> listCategoryForCustomer() {
        List<CategoryVO> categoryVOList = categoryService.listForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }
}
